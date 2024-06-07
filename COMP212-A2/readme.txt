采用Socket方式

先运行 Server.java，在运行Client.java

Client启动时会选择算法类型，以及Processor数量，会把这些数据告诉Server端

Server端在判断算法状态时，会不断到Client请求Processor执行并返回执行数据，直到最后选出Leader.

Server发送命令：
numOfProcessors
getRound
exeHSProcess <ProcessIndex>
exeLCRProcess <ProcessIndex>
sendHSMessage
sendLCRMessage
incRound

Client相应的接收上面的命令并执行