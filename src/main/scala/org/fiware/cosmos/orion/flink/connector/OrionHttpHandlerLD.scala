package org.fiware.cosmos.orion.flink.connector
import io.netty.buffer.ByteBufUtil
import io.netty.handler.codec.http._
import io.netty.util.CharsetUtil
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.json4s.jackson.JsonMethods.parse
import org.json4s.jackson.Serialization.write
import org.slf4j.LoggerFactory

class OrionHttpHandlerLD(sc: SourceContext[NgsiEventLD])
  extends OrionHttpHandlerInterface(sc: SourceContext[NgsiEventLD], NgsiEventLD.getClass) {

  private lazy val logger = LoggerFactory.getLogger(getClass)

  override def parseMessage(req : FullHttpRequest) : NgsiEventLD =  {

    try {
      // Retrieve headers
      val headerEntries = req.headers().entries()
      val SERVICE_HEADER = 4
      val SERVICE_PATH_HEADER = 5
      val service = headerEntries.get(SERVICE_HEADER).getValue()
      val servicePath = headerEntries.get(SERVICE_PATH_HEADER).getValue()

      // Retrieve body content and convert from Byte array to String
      val content = req.content()

      val byteBufUtil = ByteBufUtil.readBytes(content.alloc, content, content.readableBytes)
      val jsonBodyString = byteBufUtil.toString(0,content.capacity(),CharsetUtil.UTF_8)
      content.release()
      // Parse Body from JSON string to object and retrieve entities
      val dataObj = parse(jsonBodyString).extract[HttpBody]
      val parsedEntities = dataObj.data

      val subscriptionId = dataObj.subscriptionId
      val entities = parsedEntities.map(entity => {
        // Retrieve entity id
        val entityId = entity("id").toString

        // Retrieve entity type
        val entityType = entity("type").toString

        //Retrieve entity context
        val entityContext = entity.getOrElse("@context",Array("https://schema.lab.fiware.org/ld/context"))
        // Retrieve attributes
        val attrs = entity.filterKeys(x => x != "id" & x!= "type" & x!= "@context" )
          //Convert attributes to Attribute objects
          .transform((k,v) => (v.asInstanceOf[Map[String,Any]]))
        EntityLD(entityId, entityType, attrs,entityContext)
      })
      // Generate timestamp
      val creationTime = System.currentTimeMillis
      // Generate NgsiEvent
      val ngsiEvent = NgsiEventLD(creationTime, service, servicePath, entities, subscriptionId)
      ngsiEvent
    } catch {
      case e: Exception => null
      case e: Error => null
    }
  }
  override def sendMessage(msg: scala.Serializable) : Unit = {
    val ngsiEvent = msg.asInstanceOf[NgsiEventLD]
    logger.info(write(ngsiEvent))
    sc.collect(ngsiEvent)
  }
}


