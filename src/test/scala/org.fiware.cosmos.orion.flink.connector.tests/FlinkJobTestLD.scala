package org.fiware.cosmos.orion.flink.connector.tests

import org.apache.flink.streaming.api.scala._
import org.apache.flink.streaming.api.scala.StreamExecutionEnvironment
import org.apache.flink.streaming.api.windowing.time.Time
import org.fiware.cosmos.orion.flink.connector._
import org.json4s.DefaultFormats
import org.json4s.jackson.Serialization.write


object ConstantsLD {
  final val Port = 9002
  final val MaxWindow = 5
  final val MinWindow = 2
}

/**
  * Example1 Orion Connector
  * @author @sonsoleslp
  */
object FlinkJobTest{
  implicit val formats = DefaultFormats

  def main(args: Array[String]): Unit = {
    val env = StreamExecutionEnvironment.getExecutionEnvironment
    // Create Orion Source. Receive notifications on port 9001
    val eventStream = env.addSource(new OrionSourceLD(ConstantsLD.Port))
    // Process event stream
    val processedDataStream = eventStream
      .flatMap(event => event.entities)
      .map(entity => {
        val temp = entity.attrs("temperature").value.asInstanceOf[Number].floatValue()
        val pres = entity.attrs("pressure").value.asInstanceOf[Number].floatValue()
        EntityNode(entity.id, temp, pres)
      })
      .keyBy("id")
      .timeWindow(Time.seconds(ConstantsLD.MaxWindow), Time.seconds(ConstantsLD.MinWindow))

      processedDataStream.max("temperature").map(max=> {
          simulatedNotification.maxTempVal = max.temperature})
      val sinkStream = processedDataStream .max("pressure").map(max=> {
        simulatedNotification.maxPresVal = max.pressure
        OrionSinkObject(write(max),"http://localhost:3001",ContentType.JSON,HTTPMethod.POST)
      })
    OrionSink.addSink(sinkStream)

    env.execute("Socket Window NgsiEvent")
  }

  case class EntityNode(id: String, temperature: Float, pressure: Float)
}
