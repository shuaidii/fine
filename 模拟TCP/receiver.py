import sys
import socket
import struct

import Helper


class Receiver(object):
    def __init__(self, filename, listening_port, address_for_acks, port_for_acks):
        self.filename = open(filename, 'w+')
        self.listening_port = listening_port
        self.address_for_acks = address_for_acks
        self.port_for_acks = port_for_acks
        self.expected_seq_number = 1

        self.log = Helper.Log(self.port_for_acks, self.listening_port)

        self.createSocket()

    def createSocket(self):
        receive_socket = socket.socket(socket.AF_INET, socket.SOCK_DGRAM)
        receive_socket.setsockopt(socket.SOL_SOCKET, socket.SO_REUSEADDR, 1)
        receive_socket.bind(('localhost', self.listening_port))

        ack_socket = socket.socket(socket.AF_INET, socket.SOCK_STREAM)
        address = (self.address_for_acks, self.port_for_acks)
        opened = False
        while True:
            data, addr = receive_socket.recvfrom(1024)
            sport, dport, seq, ack, off, flags, win, expected_checksum, urp = struct.unpack("!HHLLBBHHH", data[:20])
            tcp_header = struct.pack("!HHLLBBHHH", sport, dport, seq, ack, off, flags, win, 0, urp)
            msg = data[20:]
            checksum = Helper.checksum(tcp_header, msg)

            if checksum != expected_checksum:
                print("Checksum failed")
            else:
                if self.expected_seq_number == seq:
                    opened = self.write(ack_socket, address, flags, msg, opened, seq)

    def write(self, ack_socket, address, flags, msg, opened, seq):
        self.filename.write(msg)
        self.log.setSequence(self.expected_seq_number)
        self.log.setAck(self.expected_seq_number)
        self.expected_seq_number += len(msg)
        try:
            if not opened:
                ack_socket.connect(address)
                opened = True
            ack_socket.sendall(str(seq))
            if flags == 1:
                print("Delivery completed successfully")
                self.filename.close()
                self.log.fin = 1
                self.log.display()
                sys.exit()
            self.log.display()
        except Exception as error:
            print("Caught: %s", error)

        return opened


if __name__ == "__main__":
    if len(sys.argv) != 5:
        print("python receiver.py <filename> <listening_port> <address_for_acks> <port_for_acks>")
        sys.exit(1)

    filename = str(sys.argv[1])
    listening_port = int(sys.argv[2])
    address_for_acks = sys.argv[3]
    port_for_acks = int(sys.argv[4])
    Receiver(filename, listening_port, address_for_acks, port_for_acks)
