import java.io.File;
import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UdpServer extends Thread {
    private DatagramSocket dsock = null;
    private boolean closed = false;
    private static int rate = 0;

    public UdpServer(int udpPort) throws IOException {
        dsock = new DatagramSocket(udpPort);
    }

    @Override
    public void run() {
        byte arr1[] = new byte[150];
        while (!closed) {
            try {
                DatagramPacket datagramPacket = new DatagramPacket(arr1, arr1.length);
                dsock.receive(datagramPacket);

                // 异步线程进行vod传输
                new Tranport(datagramPacket).start();

            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    class Tranport extends Thread {
        public DatagramPacket datagramPacket;

        public Tranport(DatagramPacket datagramPacket) {
            this.datagramPacket = datagramPacket;
        }

        @Override
        public void run() {
            try {
                String path = new String(datagramPacket.getData(), 0, datagramPacket.getLength());
                System.out.println("UdpServer: receive to get vod: " + path);

                File file = new File(path);
                if (!file.exists()) {
                    // 文件不存在
                    byte[] bytes = "404 Not Found".getBytes(StandardCharsets.UTF_8);
                    datagramPacket.setData(bytes);
                    dsock.send(datagramPacket);
                    return;
                }

                // 确定发送比特率，后续进行分页
                byte[] bytes = FileUtil.readFileBytes(path);
                int rateByte = (int) Math.ceil(rate * 1.0D / 8);
                if (rateByte == 0) {
                    rateByte = 102400/2;
                }

                int totalByteSize = bytes.length;   // 总大小
                int startByteIndex = 0; // vod当前byte写入位置
                int pageIndex = 0; // vod 当前分页
                int totalPage = (int) Math.ceil(totalByteSize * 1.0D / rateByte); // 总分页数

                for (int i = 0; i < totalByteSize; ) {
                    byte[] headerBytes = (totalByteSize + " " + startByteIndex + " " + pageIndex + " " + totalPage + " ").getBytes(StandardCharsets.UTF_8);
                    byte[] vodPageBytes = new byte[rateByte];
                    if (totalByteSize - i < rateByte) {
                        // 最后一页
                        vodPageBytes = new byte[totalByteSize - i];
                    }
                    // 每页数据
                    for (int j = 0; j < rateByte && i < totalByteSize; j++, i++) {
                        vodPageBytes[j] = bytes[i];
                    }

                    // 分页发送
                    datagramPacket.setData(mergeBytes(headerBytes, vodPageBytes));
                    dsock.send(datagramPacket);
                    System.out.printf("UdpServer: send [vode=%s][page=%s/%s][rate=%d]%n", path, pageIndex + 1, totalPage, rate);

                    startByteIndex += vodPageBytes.length;
                    pageIndex++;

                    int sleepMills = rate > 0 ? 1000 : 5;
                    // sleep 1s
                    try {
                        Thread.sleep(sleepMills);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            } catch (Exception e) {
                e.printStackTrace();
            }


        }
    }

    private byte[] mergeBytes(byte[] headerBytes, byte[] vodPageBytes) {
        byte[] bytes = new byte[headerBytes.length + vodPageBytes.length];
        for (int i = 0; i < headerBytes.length; i++) {
            bytes[i] = headerBytes[i];
        }
        for (int i = 0; i < vodPageBytes.length; i++) {
            bytes[i + headerBytes.length] = vodPageBytes[i];
        }
        return bytes;
    }

    public void close() {
        closed = true;
    }

    public static void setRate(int rate) {
        UdpServer.rate = rate;
    }
}