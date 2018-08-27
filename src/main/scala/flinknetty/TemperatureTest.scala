
package flinknetty
//import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

import connector.{ TcpReceiverSource, HttpReceiverSource}

import org.apache.flink.streaming.api.windowing.time.Time

object TemperatureTest {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text = env.addSource(new HttpReceiverSource(9001)) // HTTP server not register on any third party

    val windowCounts = text
//      .flatMap { w => w.split("\\s") }
        .map(w => println(w))
      .map { w => TemperatureTestObj(2) }
      .keyBy("temp")
      .timeWindow(Time.seconds(5), Time.seconds(1))
      .sum("temp")

    // print the results with a single thread, rather than in parallel
    windowCounts.print().setParallelism(1)
    env.execute("Socket Window WordCount")
  }

  // Data type for words with count
  case class TemperatureTestObj(temp: Long)
}
