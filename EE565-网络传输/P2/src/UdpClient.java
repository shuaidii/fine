import java.io.IOException;
import java.net.*;
import java.nio.charset.StandardCharsets;
import java.util.*;

public class UdpClient {
    public static void main(String args[]) throws Exception {
        InetAddress add = InetAddress.getByName("localhost");

        DatagramSocket dsock = new DatagramSocket();
        String message1 = "This is client calling";
        byte arr[] = message1.getBytes();
        DatagramPacket dpack = new DatagramPacket(arr, arr.length, add, 7077);
        dsock.send(dpack);                                   // send the packet
        Date sendTime = new Date();                          // note the time of sending the message

        dsock.receive(dpack);                                // receive the packet
        String message2 = new String(dpack.getData());
        Date receiveTime = new Date();   // note the time of receiving the message
        System.out.println((receiveTime.getTime() - sendTime.getTime()) + " milliseconds echo time for " + message2);
    }

    /**
     * 通过UDP下载vod
     *
     * @param host
     * @param port
     * @param path
     * @return
     * @throws IOException
     */
    public static byte[] load(String host, int port, String path) throws IOException {
        byte[] vodPathByte = path.getBytes(StandardCharsets.UTF_8);
        InetAddress address = InetAddress.getByName(host);
        DatagramSocket datagramSocket = new DatagramSocket();
        DatagramPacket datagramPacket = new DatagramPacket(vodPathByte, vodPathByte.length, address, port);
        datagramSocket.setSoTimeout(3000);

        datagramSocket.send(datagramPacket);

        byte[] vodBytes = null;
        int receiveTotalPage = 0;
        int receiveByteCount = 0;
        long startTime = System.currentTimeMillis();
        try {
            System.out.println("UdpClient: start to get vod: " + path);
            while (true) {
                datagramPacket.setData(new byte[102400]);
                datagramSocket.receive(datagramPacket);
                byte[] receiveBytes = datagramPacket.getData();
                String vodSizeStr = new String(receiveBytes, 0, datagramPacket.getLength(), StandardCharsets.UTF_8);
                if (vodSizeStr.equals("404 Not Found")) {
                    return null;
                }

                String[] infos = vodSizeStr.split(" ");
                int totalByteSize = Integer.parseInt(infos[0]); // vod总大小
                int startByteIndex = Integer.parseInt(infos[1]);// vod当前byte写入位置
                int pageIndex = Integer.parseInt(infos[2]); // vod 当前分页
                int totalPage = Integer.parseInt(infos[3]); // vod 总分页
                receiveTotalPage++;

                String info = totalByteSize + " " + startByteIndex + " " + pageIndex + " " + totalPage + " ";
                int receiveByteStartIndex = info.getBytes(StandardCharsets.UTF_8).length;
                receiveByteCount += datagramPacket.getLength() - receiveByteStartIndex;
                TCPServerHandler.VOD_LOAD_RATE_MAP.put(path, (receiveByteCount * 8D * 1000) / (System.currentTimeMillis() - startTime));

                System.out.printf("UdpClient: receive [totalByteSize=%d][startByteIndex=%d][pageIndex=%d][totalPage=%d][receiveTotalPage=%d]%n",
                        totalByteSize, startByteIndex, pageIndex, totalPage, receiveTotalPage);

                if (vodBytes == null) {
                    vodBytes = new byte[totalByteSize];
                }

                // vod 分页数据

                for (int i = 0; i < datagramPacket.getLength() - receiveByteStartIndex; i++) {
                    vodBytes[startByteIndex + i] = receiveBytes[receiveByteStartIndex + i];
                }

                // 更新下载进度
                TCPServerHandler.VOD_LOAD_PERCENTAGE_MAP.put(path, (int) (receiveTotalPage * 100D / totalPage));
                if (receiveTotalPage == totalPage) {
                    break;
                }
            }

            // 更新下载进度
            TCPServerHandler.VOD_LOAD_PERCENTAGE_MAP.put(path, 100);

        } catch (Exception e) {
            e.printStackTrace();
        }

        datagramSocket.close();

        return vodBytes;
    }
}