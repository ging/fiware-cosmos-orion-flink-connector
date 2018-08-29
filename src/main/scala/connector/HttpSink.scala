package connector

import org.apache.flink.streaming.api.scala.DataStream
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.entity.StringEntity
import org.apache.http.client.HttpClient
import org.apache.http.client.methods.HttpGet
import org.apache.http.impl.client.HttpClientBuilder
class HttpSink () {
}

object HttpSink {
   def addSink(url: String, stream: DataStream[String]): Unit = {

     stream.addSink( msg =>{
       val post = new HttpPost(url)
       post.setHeader("Content-type", "application/json")
       post.setEntity(new StringEntity(msg))

       val client = HttpClientBuilder.create.build

       try {
         val response = client.execute(post)
         println("POST to "+ url)
       } catch {
         case e: Exception => {
           println("ERROR Sending")
         }
       }

     })

   }
}