运行：
java AggregationServer 4567
java ContentServer 127.0.0.1:4567 weather.txt
也可以运行多个ContentServer，但最好在多台电脑上（AggregationServer判断前后接入的ContentServer是不用同一个，用的是ContentServer的运行电脑IP（关于201与200的返回）)
java GETClient 127.0.0.1:4567 IDS60901

对于“In each case, the participants will be guaranteed that this order is maintained if they are using Lamport clocks.”：
通过 LamportPriorityExecute.java 实现
1. PUT与GET请求中，都会携带自身的Lamport值到AggregationServer
2. AggregationServer中把PUT与GET请求要处理的逻辑封装成LamportPriorityMessage消息体（AggregationServerSocket.processGet与processPut方法），再把消息送入LamportPriorityExecute中的优先级阻塞队列中
3. LamportPriorityExecute是一个单线程，run方法会循环从优先级阻塞队列中拿到lamport值最小的消息体，再执行消息逻辑（LamportPriorityMessage.execute方法）
所以AggregationServer同时收到多个请求时，按lamport从小到大处理