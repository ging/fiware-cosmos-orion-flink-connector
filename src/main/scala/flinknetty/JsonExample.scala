package flinknetty
import org.apache.flink.streaming.api.scala._

import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time

import connector.{HttpReceiverSource, TcpReceiverSource, HttpHandler}

import org.json4s._
import org.json4s.jackson.Serialization
import org.json4s.jackson.Serialization.read
import org.json4s.jackson.Serialization.write
import org.json4s.jackson.JsonMethods._

object JsonExample {
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
    val source = new HttpReceiverSource(9001)
    val text = env.addSource(source)
//    val text = env.addSource(new TcpReceiverSource( 9001, Some("http://localhost:3000/")))
    //  val text = env.socketTextStream( "localhost", 9000, '\n' );

    val windowCounts = text
//      .flatMap {w => w.split("\\n") }
//     .map { w => read[NgsiEvent](w) }
//     .map { w => read[Iterable[NgsiEvent]](w) }
//          .keyBy("word")
//          .timeWindow(Time.seconds(5), Time.seconds(1))
//          .sum("count")

//    val windowCounts = text
//      .flatMap { w => w.split("\\s") }
//      .map { w => WordWithCount(w, 1) }
//      .keyBy("word")
//      .timeWindow(Time.seconds(5), Time.seconds(1))
//      .sum("count")

    // print the results with a single thread, rather than in parallel
    windowCounts.print().setParallelism(1)
    env.execute("Socket Window WordCount")
  }
}
