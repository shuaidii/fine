./newudpl -i127.0.0.1 -o127.0.0.1 -L 50

python receiver.py output.txt 41194 127.0.0.1 41191

python sender.py input.txt 127.0.0.1 41192 1024 41191