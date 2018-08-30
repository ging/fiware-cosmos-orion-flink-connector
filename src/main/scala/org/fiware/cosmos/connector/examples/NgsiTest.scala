package org.fiware.cosmos.connector.examples

import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.time.Time
import org.fiware.cosmos.connector._
import org.json4s._

/**
  * Test class for Orion Connector
  * @author @sonsoleslp
  */
object NgsiTest {
  final val URL_CB = "http://localhost:3000/v2/entities/"
  final val CONTENT_TYPE = ContentType.JSON
  final val METHOD = HTTPMethod.POST

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    val eventStream = env.addSource(new OrionSource(9001))

    val processedDataStream = eventStream
      .flatMap(event => event.entities)
      .map(entity =>
        new Temp_Node(
          entity.id,
          entity.attrs("temperature").value.asInstanceOf[Number].floatValue())
        )
      .keyBy("id")
      .timeWindow(Time.seconds(5), Time.seconds(1))
      .min("temperature")
      .map(tempNode => {
        val url = URL_CB + tempNode.id + "/attrs"
        OrionSinkObject(tempNode.toString, url, CONTENT_TYPE, METHOD)
      })

    OrionSink.addSink( processedDataStream )

    // print the results with a single thread, rather than in parallel
    processedDataStream.map(orionSinkObject => orionSinkObject.content).print().setParallelism(1)


    env.execute("Socket Window NgsiEvent")
  }

  case class Temp_Node(id: String, temperature: Float) extends  Serializable {
     override def toString :String = { "{\"id\": \""+id+"\", \"temperature\": "+temperature+"}" }
  }
}
