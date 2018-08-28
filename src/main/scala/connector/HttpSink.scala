package connector

import org.apache.flink.streaming.api.scala.DataStream
import org.apache.http.client.methods.HttpPost
import org.apache.http.impl.client.DefaultHttpClient
import org.apache.http.entity.StringEntity

class HttpSink () {
}

object HttpSink {
   def addSink(url: String, stream: DataStream[String]): Unit = {

     stream.addSink( msg =>{
        println("POSTING...", msg)
       val post = new HttpPost(url)
       post.setHeader("Content-type", "application/json")
       post.setEntity(new StringEntity("{\"key\": \"value\"}"))
       val client = new DefaultHttpClient()
       val response = client.execute(post)
     })

   }
}