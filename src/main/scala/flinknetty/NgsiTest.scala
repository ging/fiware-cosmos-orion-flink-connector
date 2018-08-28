
package flinknetty
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import connector.{HttpReceiverSource, HttpSink, TcpReceiverSource}
import org.apache.flink.streaming.api.windowing.time.Time
import org.json4s._
import org.json4s.jackson.JsonMethods._

object NgsiTest {
  implicit val formats = DefaultFormats

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val text = env.addSource(new HttpReceiverSource(9001))

    // val msg = text.map(a=>a.header.get(3).getKey()).map(o=>println(o))

    val windowCounts = text
      .map{ event => parse(event.body).extract[DataClass] }
      .flatMap(body => body.data)
      .map(el => new Temp_Node(el.id, el.temperature.value.asInstanceOf[Number].floatValue()))
      .keyBy("id")
      .timeWindow(Time.seconds(5), Time.seconds(1))
      .min("temperature")

    HttpSink.addSink("http://localhost:3000", windowCounts.map(x => x.toString))

    // print the results with a single thread, rather than in parallel
    windowCounts.print().setParallelism(1)
    env.execute("Socket Window NgsiEvent")
  }
  case class DataClass(data: Seq[Node], subscriptionId: String)
  case class Node(id: String, `type`: String, value: Any, metadata: Any, co: Attr, co2: Attr, humidity: Attr, pressure: Attr, temperature: Attr,  wind_speed: Attr)
  case class Attr(`type`: String, value: Any, metadata: Any)
  case class CO_Node(id: String, co: Float)
  case class Temp_Node(id: String, temperature: Float)
}
