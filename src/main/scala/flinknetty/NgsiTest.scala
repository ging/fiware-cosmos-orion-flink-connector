package flinknetty

import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.{DataStream, StreamExecutionEnvironment}
import connector.{HttpReceiverSource, HttpSink, TcpReceiverSource, ContentType, Method, HttpSinkObject}
import org.apache.flink.streaming.api.windowing.time.Time
import org.json4s._
import org.json4s.jackson.JsonMethods._

object NgsiTest {
  implicit val formats = DefaultFormats
  final val URL_CB = "http://localhost:3000/v2/entities/"
  final val CONTENT_TYPE = ContentType.JSON
  final val METHOD = Method.POST

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val eventStream = env.addSource(new HttpReceiverSource(9001))

    val processedDataStream = eventStream
//      .map( event => parse(event.body).extract[DataClass] )
//      .flatMap(body => body.data)
//      .map(el => new Temp_Node(el.id, el.temperature.value.asInstanceOf[Number].floatValue())
      .map(event => new Temp_Node(event.entityId,event.attrs("temperature").asInstanceOf[Number].floatValue()))
      .keyBy("id")
      .timeWindow(Time.seconds(5), Time.seconds(1))
      .min("temperature")
      .map(x => HttpSinkObject(x.toString, URL_CB + x.id +"/attrs", CONTENT_TYPE, METHOD))

    // URL from header¿¿¿¿???????
    HttpSink.addSink( processedDataStream )

    // print the results with a single thread, rather than in parallel
    processedDataStream.map(z => z.content).print().setParallelism(1)
    env.execute("Socket Window NgsiEvent")
  }

  case class DataClass(data: Seq[Node], subscriptionId: String)
  case class Node(id: String, `type`: String, value: Any, metadata: Any, co: Attr, co2: Attr, humidity: Attr, pressure: Attr, temperature: Attr,  wind_speed: Attr)
  case class Attr(`type`: String, value: Any, metadata: Any)
  case class Temp_Node(id: String, temperature: Float) extends  Serializable {
     override def toString :String = { "{\"id\": \""+id+"\", \"temperature\": "+temperature+"}" }
  }
}
