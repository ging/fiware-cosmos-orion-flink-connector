/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements.  See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License.  You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package connector

import io.netty.buffer.{ByteBufUtil, Unpooled}
import io.netty.channel.{ChannelFutureListener, ChannelHandlerContext, ChannelInboundHandlerAdapter}
import io.netty.handler.codec.http.HttpResponseStatus._
import io.netty.handler.codec.http.HttpVersion._
import io.netty.handler.codec.http._
import io.netty.util.{AsciiString, CharsetUtil}
import org.apache.flink.streaming.api.functions.source.SourceFunction.SourceContext
import org.slf4j.LoggerFactory
import org.json4s._
import org.json4s.jackson.JsonMethods._
/**
 * http server handler, process http request
 *
 * @param sc       Flink source context for collect received message
 */
class HttpHandler(
  sc: SourceContext[String]
) extends ChannelInboundHandlerAdapter {

  private lazy val logger = LoggerFactory.getLogger(getClass)
  private lazy val CONTENT_TYPE = new AsciiString("Content-Type")
  private lazy val CONTENT_LENGTH  = new AsciiString("Content-Length")

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = ctx.flush
  implicit val formats = DefaultFormats

  override def channelRead(ctx: ChannelHandlerContext, msg: AnyRef): Unit = {
    var jsonString2 = ""
    println("Nuevo mensaje", msg)
    msg match {
      case reqq : FullHttpRequest =>
        println(reqq, reqq.headers(), reqq.content())
      case request: DefaultLastHttpContent =>
        val content = request.content()
        val bbu = ByteBufUtil.readBytes(content.alloc, content, content.readableBytes)
        val jsonString = bbu.toString(0,content.capacity(),CharsetUtil.UTF_8)
        jsonString2 = jsonString
        //  val body =   parse(jsonString).extract[BodyObject]
        //  println(headers.getAll("Content-Length"))
//        sc.collect(jsonString)

      case req:  HttpRequest =>
        val j = new DefaultLastHttpContent()
        val content = j.touch(req).content()
        val bbu = ByteBufUtil.readBytes(content.alloc, content, content.readableBytes)
        val jsonString = bbu.toString(0, content.capacity(), CharsetUtil.UTF_8)
//        println("HERE!!!!!!!!!!!!!", jsonString, req.headers().get("Content-Length"))
        println(jsonString2)
        jsonString2 += req.headers().get("Content-Length")
        println(jsonString2)
        if (HttpUtil.is100ContinueExpected(req)) {
          ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE))
        }

        val keepAlive: Boolean = HttpUtil.isKeepAlive(req)

        if (!keepAlive) {
          ctx.writeAndFlush(buildResponse()).addListener(ChannelFutureListener.CLOSE)
        } else {
          val decoder = new QueryStringDecoder(req.uri)
          val param: java.util.Map[String, java.util.List[String]] = decoder.parameters()
          ctx.writeAndFlush(buildResponse())
        }
      case x =>
        logger.info("unsupported request format " + x)
    }
  }

  private def buildResponse(content: Array[Byte] = Array.empty[Byte]): FullHttpResponse = {
    val response: FullHttpResponse = new DefaultFullHttpResponse(
      HTTP_1_1, OK, Unpooled.wrappedBuffer(content)
    )
    response.headers.set(CONTENT_TYPE, "text/plain")
    response.headers.setInt(CONTENT_LENGTH, response.content.readableBytes)
    response
  }

  override def exceptionCaught(ctx: ChannelHandlerContext, cause: Throwable): Unit = {
    logger.error("channel exception " + ctx.channel().toString, cause)
    ctx.close
  }

  case class BodyObject(subscriptionId:Long, data: Seq[Any])
  case class SerializationObject(body: String, service: String, agent: String, servicePath: String)
}
