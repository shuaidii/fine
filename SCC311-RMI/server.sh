javac *.java

rmiregistry &
sleep 2

java Server &
sleep 1

java Client  5
sleep 1

