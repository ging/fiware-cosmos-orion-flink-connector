# fiware-cosmos-orion-flink-connector

This is an example for using the Netty Connector with Flink
It needs an HTTP server to register to. In this example a server implemented with node is provided (index.js). In order to run it do:

```
node index.js
```
## WordCount example with socket

Run the WordCount.scala file in IntelliJ

To see the word count in action perform an HTTP request to ```http://localhost:9000?msg=words to be counted``` and check the logs.

## HTTP server with msg query param
The next example is a NgsiEvent serialized received in an HTTP request like the previous one. Example: ```http://localhost:9000?msg=%7B+%22creationTime%22%3A+228930314431312345%2C%0D%0A++%22fiwareService%22%3A%22rooms%22%2C%0D%0A++%22fiwareServicePath%22%3A%22unknown%22%2C%0D%0A++%22timestamp%22%3A228930314431312345%2C%0D%0A++%22entityType%22%3A%22room%22%2C%0D%0A++%22entityPattern%22%3A%221%22%2C%0D%0A++%22entityId%22%3A%221%22%2C%0D%0A++%22attrs%22%3A+%5B%7B%22name%22%3A+%22room1%22%2C+%22attType%22%3A+%22temperature%22%2C+%22value%22%3A+22%7D%2C%7B%22name%22%3A+%22room2%22%2C+%22attType%22%3A+%22temperature%22%2C+%22value%22%3A+14%7D%5D%2C%0D%0A++%22count%22%3A2+%7D```
Run the JsonExample.scala file in IntelliJ

## TCP server with HTTP message parsing
*In progress*

The following approach uses a TCPReceiver from the flink-netty-connector in order to gain access of all the fields in the HTTP Header, and not only to the query params specified. 
Run the JsonExampleTCP.scala file in IntelliJ
