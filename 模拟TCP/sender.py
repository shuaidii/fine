import sys
import socket
import Helper
import os
import time
import select


class Sender(object):
    def __init__(self, filename, address_of_udpl, port_number_of_udpl, window_size, ack_port_number):

        self.filename = filename
        self.address_of_udpl = address_of_udpl
        self.port_number_of_udpl = port_number_of_udpl
        self.ack_port_number = ack_port_number
        self.window_size = window_size
        self.file = open(self.filename)

        self.log = Helper.Log(self.port_number_of_udpl, self.ack_port_number, True)

        self.MSS = 576
        self.next_seq_num = 1
        self.fin = 0

        self.filesize = os.stat(filename).st_size
        self.num_packets = int(self.filesize / self.MSS) + (self.filesize % self.MSS > 0)

        self.udt_socket = None
        self.ack_socket = None
        self.start_tcp()

    def start_tcp(self):
        self.udt_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM, socket.IPPROTO_UDP)
        self.udt_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)

        self.ack_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        self.ack_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        try:
            self.ack_socket.bind(('localhost', self.ack_port_number))
        except socket.error as e:
            print("Socket Error %s" % e)

        self.udt_send()

    def udt_send(self):
        connected = False
        client_socket = None

        self.ack_socket.listen(6)
        estimated_rtt = 2
        num_packets = 0
        stats = Stats()

        while self.next_seq_num < self.filesize:
            num_packets += 1
            if num_packets == self.num_packets:
                self.fin = 1
                self.fin = 1
                self.log.fin = 1

            timeout_interval, estimated_rtt = RTT().nextUpdate(estimated_rtt)
            pkt, length = self.make_pkt()
            self.send(pkt)

            send_time = time.time()
            self.log.sequence(self.next_seq_num)
            self.next_seq_num += length
            stats.byteIncr(int(length))
            stats.segmentIncr()
            if not connected:
                client_socket, clientaddress = self.ack_socket.accept()
                connected = True
            estimated_rtt = self.receive_ack(estimated_rtt, timeout_interval, client_socket, length, pkt, send_time,
                                             stats)
        stats.displayStats()

    def receive_ack(self, estimated_rtt, timeout_interval, client_socket, length, pkt, send_time, stats):
        while True:
            r, a, b = select.select([client_socket], [], [], timeout_interval)
            if r:
                data = client_socket.recv(1024)
                self.log.setEstimatedRTT(estimated_rtt)
                self.log.ack(data)
                self.log.display()
                estimated_rtt = time.time() - send_time
                break

            self.send(pkt)
            stats.byteIncr(int(length))
            stats.segmentIncr()
            stats.restransmitted()
        return estimated_rtt

    def make_pkt(self):
        self.file.seek(self.next_seq_num - 1)
        msg = self.file.read(self.MSS)

        packet = Helper.Packet(self.ack_port_number, self.port_number_of_udpl, self.next_seq_num, self.fin, msg)
        return packet.pack(), len(msg)

    def send(self, pkt):
        self.udt_socket.sendto(pkt, (self.address_of_udpl, self.port_number_of_udpl))


class Stats:
    def __init__(self):
        self.total_send_bytes = 0
        self.send_segments = 0
        self.segments_retransmitted = 0

    def byteIncr(self, length):
        self.total_send_bytes += length

    def segmentIncr(self):
        self.send_segments += 1

    def restransmitted(self):
        self.segments_retransmitted += 1

    def displayStats(self):
        print("Delivery completed successfully")
        print("Total bytes sent = %s" % self.total_send_bytes)
        print("Segments sent = %s" % self.send_segments)
        print("Segments retransmitted = %s" % self.segments_retransmitted)


class RTT:
    def __init__(self):
        self.sample_RTT = 2
        self.dev_RTT = 0
        self.timeout_interval = 1

    def nextUpdate(self, estimated_RTT):
        estimated_RTT = 0.875 * estimated_RTT + 0.125 * self.sample_RTT

        self.dev_RTT = 0.75 * self.dev_RTT + 0.25 * (self.sample_RTT - estimated_RTT)
        self.timeout_interval = estimated_RTT + 4 * self.dev_RTT
        return self.timeout_interval, estimated_RTT


if __name__ == "__main__":
    if len(sys.argv) != 6:
        print("python sender.py <file> <address_of_udpl> <port_number_of_udpl> <window_size> <ack_port_number>")
        sys.exit(1)

    filename = str(sys.argv[1])
    address_of_udpl = sys.argv[2]
    port_number_of_udpl = int(sys.argv[3])
    window_size = int(sys.argv[4])
    ack_port_number = int(sys.argv[5])

    Sender(filename, address_of_udpl, port_number_of_udpl, window_size, ack_port_number)
