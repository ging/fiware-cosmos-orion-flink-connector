package connector

import connector.ContentType.Plain
import org.apache.flink.streaming.api.scala.DataStream
import org.apache.http.client.methods._
import org.apache.http.entity.StringEntity
import org.apache.http.client.HttpClient
import org.apache.http.impl.client.HttpClientBuilder
import org.slf4j.LoggerFactory


case class HttpSinkObject(content: String, url: String, contentType: ContentType.Value, method: Method.Value)

object ContentType extends Enumeration {
  type ContentType = Value
  val JSON = Value("application/json")
  val Plain = Value("text/plain")
}
object Method extends Enumeration {
  type Method = Value
  val POST = Value("HttpPost")
  val PUT = Value("HttpPut")
  val PATCH = Value("HttpPatch")

}


class HttpSink () {
}


object HttpSink {
  private lazy val logger = LoggerFactory.getLogger(getClass)
  def getMethod(method: Method.Value, url: String): HttpEntityEnclosingRequestBase = {
    method match {
      case Method.POST => new HttpPost(url)
      case Method.PUT => new HttpPut(url)
      case Method.PATCH => new HttpPatch(url)
    }
  }
  def addSink( stream: DataStream[HttpSinkObject]): Unit = {

     stream.addSink( msg => {

       val httpEntity = getMethod(msg.method, msg.url)
       httpEntity.setHeader("Content-type", msg.contentType.toString)
       httpEntity.setEntity(new StringEntity(msg.content))

       val client = HttpClientBuilder.create.build

       try {
         val response = client.execute(httpEntity)
          logger.info("POST to "+ msg.url)
       } catch {
         case e: Exception => {
           logger.error(e.toString)
         }
       }

     })

   }

}

