import struct
import time


class Packet(object):
    def __init__(self, ack_port_num, remote_port, seq, fin, msg):
        self.sport = ack_port_num
        self.dport = int(remote_port)
        self.seq = int(seq)
        self.ack = 0
        self.off = 0

        self.FIN = fin  # Set to 1 if last packet
        self.SYN = 0
        self.RST = 0
        self.PUSH = 0
        self.ACK = 0
        self.URG = 0

        self.win_size = 0
        self.sum = 0
        self.urp = 0
        self.msg = msg

    def pack(self):
        self.flags = self.FIN + (self.SYN << 1) + (self.RST << 2) + (self.PUSH << 3) + \
                     (self.ACK << 4) + (self.URG << 5)
        tcp_header = struct.pack("!HHLLBBHHH", self.sport, self.dport, self.seq,
                                 self.ack, self.off, self.flags, self.win_size, self.sum, self.win_size)
        self.sum = checksum(tcp_header, self.msg)
        tcp_header = struct.pack("!HHLLBBHHH", self.sport, self.dport, self.seq,
                                 self.ack, self.off, self.flags, self.win_size, self.sum, self.win_size)

        packet = tcp_header + self.msg
        return packet


class Log(object):
    def __init__(self, source, destination, estimated_rtt_bit=False):
        self.source = source
        self.destination = destination
        self.fin = 0
        self.estimatedRTT_bit = estimated_rtt_bit

        self.sequence = None
        self.ack = None
        self.estimate = None

    def setSequence(self, sequence):
        self.sequence = "Sequence " + str(sequence)

    def setAck(self, ack):
        self.ack = "ACK " + str(ack)

    def setEstimatedRTT(self, estimated_rtt):
        self.estimate = "EST_RTT " + str(estimated_rtt)

    def display(self):
        log = "{0} Source {1} Destination {2} {3} {4} Fin {5}".format(
            str(time.time()), str(self.source), str(self.destination), str(self.sequence), str(self.ack), str(self.fin))
        if self.estimatedRTT_bit:
            log = str(log) + " " + str(self.estimate)

        print(log)


def checksum(tcp_header, msg):
    sum_num = 0
    msg = tcp_header.decode("utf-8") + msg
    for i in range(0, len(msg), 2):
        w = (ord(msg[i]) << 8) + (ord(msg[i + 1]))
        sum_num = sum_num + w
        sum_num = (sum_num >> 16) + (sum_num & 0xffff);
        sum_num = ~sum_num & 0xffff
    return sum_num
