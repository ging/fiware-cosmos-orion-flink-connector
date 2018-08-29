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
  sc: SourceContext[NgsiEvent]
) extends ChannelInboundHandlerAdapter {

  private lazy val logger = LoggerFactory.getLogger(getClass)
  private lazy val CONTENT_TYPE = new AsciiString("Content-Type")
  private lazy val CONTENT_LENGTH  = new AsciiString("Content-Length")

  override def channelReadComplete(ctx: ChannelHandlerContext): Unit = ctx.flush
  implicit val formats = DefaultFormats

  override def channelRead(ctx: ChannelHandlerContext, msg: AnyRef): Unit = {
    msg match {
      case req : FullHttpRequest =>
        val content = req.content()
        val byteBufUtil = ByteBufUtil.readBytes(content.alloc, content, content.readableBytes)
        val jsonBodyString = byteBufUtil.toString(0,content.capacity(),CharsetUtil.UTF_8)
        val headerEntries = req.headers().entries()

        val dataObj = parse(jsonBodyString).extract[DataObj]
        val entities = dataObj.data
        val entity = entities(0)
        val entityId = entity("id").toString
        val entityType = entity("type").toString
        val attrs2 = entity.filterKeys(x => x != "id" & x!= "type" )//.transform((k,v) => v.asInstanceOf[Attr])
        println("22222222222222222222222222222222222222222222222222",attrs2)
//        val attrs = attrs2.mapValues(x=>x.asInstanceOf[Attr])
        val attrs = attrs2.map{ case (k, v) => println(k,v);(k, v.asInstanceOf[Attr])}
       println(attrs)

        val ngsiEvent = new NgsiEvent(
          System.currentTimeMillis, // creationTime
          headerEntries.get(4).getValue(), // service
          headerEntries.get(5).getValue(), // servicePath
          entityType, // entityType
          entityId, // entityId
          attrs // attrs
        )

        println(ngsiEvent)
        sc.collect(ngsiEvent)

        if (HttpUtil.is100ContinueExpected(req)) {
          ctx.write(new DefaultFullHttpResponse(HTTP_1_1, CONTINUE))
        }

        val keepAlive: Boolean = HttpUtil.isKeepAlive(req)

        if (!keepAlive) {
          ctx.writeAndFlush(buildResponse()).addListener(ChannelFutureListener.CLOSE)
        } else {
          // val decoder = new QueryStringDecoder(req.uri)
          // val param: java.util.Map[String, java.util.List[String]] = decoder.parameters()
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


}