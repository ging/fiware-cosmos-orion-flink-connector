# FlinkNetty

This is an example for using the Netty Connector with Flink
It needs an HTTP server to register to. In this example a server implemented with node is provided (index.js). In order to run it do:
```
node index.js
```
Next run the WordCount.scala file in IntelliJ

To see the word count in action perform an HTTP request to ```http://localhost:9000?msg=words to be counted``` and check the logs.