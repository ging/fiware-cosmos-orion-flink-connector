package org.fiware.cosmos.orion.flink.connector

import java.util.Properties

import org.apache.flink.streaming.api.scala._
import org.apache.flink.api.common.serialization.SimpleStringSchema
import org.apache.flink.streaming.connectors.kafka.FlinkKafkaConsumer011
import org.slf4j.LoggerFactory
object FiwareUsageControl {
  private lazy val logger = LoggerFactory.getLogger(getClass)

  def start(port: Number, topic: String = "flink", env: StreamExecutionEnvironment) = {


    // Initialize OrionSource in given port
    val orionStream = env.addSource(new OrionSource(9001, "flink"))

    // Initialize Kafka Source with specified topic
    val properties = new Properties()
    properties.setProperty("bootstrap.servers", "138.4.7.94:9092")
    val usageControlConsumer = new FlinkKafkaConsumer011[String]("flink", new SimpleStringSchema(), properties)

    val kafkaStream = env.addSource(usageControlConsumer)
    kafkaStream.print()

    kafkaStream.map(e => {
      e match {
        case "ERROR" => {
          logger.error("ERROR: You are not compliant with the policies")
          throw new Exception("ERROR: You are not compliant with the policies")
        }
        case "WARNING" => logger.warn("WARNING: You are almost dead")
        case "OK" => logger.info("OK: You are compliant with the policies")
        case _ => {
        }
      }
    }).name("Usage Control Signals")


//    orionStream.print()
    orionStream.name("Orion Source Notifications")
  }
}
