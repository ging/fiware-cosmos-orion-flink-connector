
package flinknetty
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import org.apache.flink.streaming.util.serialization._
import connector.{HttpReceiverSource, TcpReceiverSource}
import org.apache.flink.streaming.api.windowing.time.Time
import org.json4s._
import org.json4s.jackson.JsonMethods._

object NgsiTest {
  implicit val formats = DefaultFormats

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text = env.addSource(new HttpReceiverSource(9001))

    val windowCounts = text
      .map { jsonStr => parse(jsonStr).extract[NgsiEvent]}
      .flatMap(event => event.attrs)
      .keyBy("name")
      .timeWindow(Time.seconds(5), Time.seconds(1))
      .min("value")

    // print the results with a single thread, rather than in parallel
    windowCounts.print().setParallelism(1)
    env.execute("Socket Window NgsiEvent")
  }
  case class Attributes( name:String,
                         attType:String,
                         value:Double )
  
  case class NgsiEvent( creationTime:Long,
                        fiwareService:String,
                        fiwareServicePath:String,
                        timestamp:Long,
                        entityType:String,
                        entityPattern:String,
                        entityId:String,
                        attrs:Iterable[Attributes],
                        count:Int )
}
