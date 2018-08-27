/*
package flinknetty
import org.apache.flink.streaming.api.functions.AscendingTimestampExtractor
import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time
import org.apache.flink.streaming.api.windowing.windows.TimeWindow

import connector.{HttpReceiverSource, TcpReceiverSource}

import org.apache.flink.table.shaded.org.joda.time.DateTime
import org.apache.flink.util.Collector
import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.read
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.JsonMethods._

object JsonExampleTCP {
  implicit val formats = DefaultFormats
  case class NgsiEvent(
                        creationTime:Long,
                        fiwareService:String,
                        fiwareServicePath:String,
                        timestamp:Long,
                        entityType:String,
                        entityPattern:String,
                        entityId:String,
                        attrs:Iterable[Attributes],
                        count:Int
                      )

  /*
    [{ creationTime: 228930314431312345,
    fiwareService:"rooms",
    fiwareServicePath:"unknown",
    timestamp:228930314431312345,
    entityType:"room",
    entityPattern:"1",
    entityId:"1",
    attrs: [{name: "room1", attType: "temperature", value: 22},{name: "room2", attType: "temperature", value: 14}],
    count:2 }]
  */

  // %7B+%22creationTime%22%3A+228930314431312345%2C%0D%0A++%22fiwareService%22%3A%22rooms%22%2C%0D%0A++%22fiwareServicePath%22%3A%22unknown%22%2C%0D%0A++%22timestamp%22%3A228930314431312345%2C%0D%0A++%22entityType%22%3A%22room%22%2C%0D%0A++%22entityPattern%22%3A%221%22%2C%0D%0A++%22entityId%22%3A%221%22%2C%0D%0A++%22attrs%22%3A+%5B%7B%22name%22%3A+%22room1%22%2C+%22attType%22%3A+%22temperature%22%2C+%22value%22%3A+22%7D%2C%7B%22name%22%3A+%22room2%22%2C+%22attType%22%3A+%22temperature%22%2C+%22value%22%3A+14%7D%5D%2C%0D%0A++%22count%22%3A2+%7D
  case class Attributes(
                         name:String,
                         attType:String,
                         value:Double
                       )
  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment;
    //  val event = env.addSource(new HttpReceiverSource("msg", 9001, Some("http://localhost:3000/")))
    //val event = env.addSource(new TcpReceiverSource( 9001, Some("http://localhost:3000/")))
    //val source = new HttpReceiverSource("msg", 9001, Some("http://localhost:3000/"))
    val source = new TcpReceiverSource( 9001, Some("http://localhost:3000/"))
    val event = env.addSource(source)
    //  val event = env.socketTextStream( "localhost", 9000, '\n' );

    val events: DataStream [NgsiEvent] = event
      .map (
        v => {
          val headers = v.get("headers")
          val body = v.get("body")
          val attrs:Iterable[Attributes]= body.get("attributes").map (
            x =>{
              Attributes(
                x.elements().next().asText(),
                x.get("type").asText(),
                x.get("value").asDouble()
              )
            })
          //NGSI event building
          NgsiEvent(
            new DateTime().getMillis,
            headers.get(0).elements().next().asText(),
            headers.get(1).elements().next().asText(),
            headers.get(2).elements().next().asLong(),
            body.get("type").asText(),
            body.get("isPattern").asText(),
            body.get("id").asText(),
            attrs,
            1
          )
        })
    val timedStream = events.assignTimestampsAndWatermarks(new AscendingTimestampExtractor[NgsiEvent] {
      override def extractAscendingTimestamp(element: NgsiEvent): Long = element.creationTime
    })
    val results:DataStream[(String,String,String,String,String,String,Int,Int)] = timedStream
      .keyBy(in => in.attrs.iterator.next().name)
      .timeWindow(Time.seconds(10))
      .apply
      {
        (
          attName:String,
          window: TimeWindow
          , event: Iterable[NgsiEvent]
          , out: Collector[(String,String,String,String,String,String,Int,Int)]
        ) =>

          out.collect(
            (event.iterator.next().creationTime.toString
              ,event.iterator.next().fiwareServicePath
              ,event.iterator.next().entityId
              ,event.iterator.next().entityType
              ,event.iterator.next().attrs.iterator.next().name
              ,event.iterator.next().attrs.iterator.next().attType
              ,event.map( _.attrs.iterator.next().value.toInt).sum/event.map( _.count).sum
              ,event.map( _.count).sum))
      }
    // print the results with a single thread, rather than in parallel
    results.print().setParallelism(1)
    env.execute("Socket Window WordCount")
  }
}
*/
