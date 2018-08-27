package flinknetty
//import org.apache.flink.api.scala._
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}

import connector.{ TcpReceiverSource, HttpReceiverSource}

import org.apache.flink.streaming.api.windowing.time.Time

object WordCount {
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment

    // val text = env.socketTextStream( "localhost", 9000, '\n' ); // Text socket
    // val text = env.addSource(new HttpReceiverSource("msg", 9001, Some("http://localhost:3000/"))) // HTTP server register third party on port 3000

    val text = env.addSource(new HttpReceiverSource("msg", 9001)) // HTTP server not register on any third party

    val windowCounts = text
      .flatMap { w => w.split("\\s") }
      .map { w => WordWithCount(w, 1) }
      .keyBy("word")
      .timeWindow(Time.seconds(5), Time.seconds(1))
      .sum("count")

    // print the results with a single thread, rather than in parallel
    windowCounts.print().setParallelism(1)
    env.execute("Socket Window WordCount")
  }

  // Data type for words with count
  case class WordWithCount(word: String, count: Long)
}
