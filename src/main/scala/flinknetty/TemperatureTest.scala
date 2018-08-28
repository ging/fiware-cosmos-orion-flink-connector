
package flinknetty
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.util.serialization._
import connector.{HttpReceiverSource, TcpReceiverSource}
import org.apache.flink.streaming.api.windowing.time.Time
import org.json4s._
import org.json4s.jackson.JsonMethods._

object TemperatureTest {
  implicit val formats = DefaultFormats

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text = env.addSource(new HttpReceiverSource(9001)) // HTTP server not register on any third party

    val windowCounts = text
      .map { w => parse(w).extract[TemperatureTestObj] }
      .keyBy("room")
      .timeWindow(Time.seconds(5), Time.seconds(1))
      .min("temp")

    // print the results with a single thread, rather than in parallel
    windowCounts.print().setParallelism(1)
    env.execute("Socket Window WordCount")
  }

  // Data type for words with count
  case class TemperatureTestObj(room: String, temp: Long)
}
