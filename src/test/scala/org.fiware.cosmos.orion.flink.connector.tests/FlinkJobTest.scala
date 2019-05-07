package org.fiware.cosmos.orion.flink.connector.test


import org.apache.flink.streaming.api.scala.{StreamExecutionEnvironment, _}
import org.apache.flink.streaming.api.windowing.time.Time
import org.fiware.cosmos.orion.flink.connector._
import org.junit.Assert._

import scala.concurrent.Await

/**
  * Example1 Orion Connector
  * @author @sonsoleslp
  */
object FlinkJobTest{

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // Create Orion Source. Receive notifications on port 9001
    val eventStream = env.addSource(new OrionSource(9102))

    // Process event stream
    val processedDataStream = eventStream
      .flatMap(event => event.entities)
      .map(entity => {
        val temp = entity.attrs("temperature").value.asInstanceOf[Number].floatValue()
        val pres = entity.attrs("pressure").value.asInstanceOf[Number].floatValue()
        new EntityNode( entity.id, temp, pres)
      })
      .keyBy("id")
      .timeWindow(Time.seconds(5), Time.seconds(2))

    processedDataStream.max("temperature").map(max=> {
      SimulatedNotification.maxTempVal = max.temperature})
    processedDataStream .max("pressure").map(max=> {
      SimulatedNotification.maxPresVal = max.pressure})

    val sinkStream = processedDataStream.max("temperature").map(el=>new OrionSinkObject(el.toString,"http://localhost",
      ContentType.JSON,HTTPMethod.POST))

    OrionSink.addSink(sinkStream)
    env.execute("Socket Window NgsiEvent")
  }

  case class EntityNode(id: String, temperature: Float, pressure: Float)
}