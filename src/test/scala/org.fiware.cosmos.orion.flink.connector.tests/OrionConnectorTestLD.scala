package org.fiware.cosmos.orion.flink.connector.tests

import java.net.InetSocketAddress

import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.handler.codec.http.{DefaultFullHttpRequest, HttpMethod, HttpVersion}
import io.netty.util.CharsetUtil
import org.apache.http.client.methods.{HttpPatch, HttpPost, HttpPut}
import org.fiware.cosmos.orion.flink.connector._
import org.junit.{Assert, Test}
import org.mockito.Mockito.mock

object UtilsLD {
  final val Port = 9003
  final val SleepTime = 20000
  final val SleepTimeShort = 6000
  final val ServerAddress = "http://localhost:9003"
  final val OrionAddress = "http://localhost:2026"
  final val ContentType = "Content-Type"
  final val ContentType2 = "Content-Type2"
  final val Content = "Content"
  final val Accept = "Accept"
  final val UserAgent = "User-Agent"
  final val Json = "application/json"
  final val Orion = "orion/0.10.0"
  final val UTF8 = "application/json; charset=utf-8"
  final val FiwareService = "Fiware-Service"
  final val FiwareServicePath = "Fiware-ServicePath"
  final val Demo = "demo"
  final val Test = "/test"
  final val BadContent = "BAD CONTENT"
  final val OtherUrl = "http://localhost:9103"

}

class OrionConnectorTestLD extends  BaseTest{
  def createMockFullHttpRequest(str: String = simulatedNotificationLD.notification()): DefaultFullHttpRequest ={
    val bytes = str.getBytes(CharsetUtil.UTF_8)
    val content = Unpooled.copiedBuffer(bytes)
    val fhr = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, UtilsLD.ServerAddress, content)
    fhr.headers().set(UtilsLD.ContentType, UtilsLD.UTF8)
    fhr.headers().set(UtilsLD.ContentType2, UtilsLD.UTF8)
    fhr.headers().set(UtilsLD.Accept, UtilsLD.Json)
    fhr.headers().set(UtilsLD.UserAgent, UtilsLD.Orion)
    fhr.headers().set(UtilsLD.FiwareService, UtilsLD.Demo)
    fhr.headers().set(UtilsLD.FiwareServicePath, UtilsLD.Test)
    fhr
  }

  def createMockFullHttpRequestGet(): DefaultFullHttpRequest ={
    val fhr = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, UtilsLD.ServerAddress)
    //  val headers = new HttpHeaders(UtilsLD.ContentType, "application/json; charset=utf-8")
    fhr.headers().set(UtilsLD.ContentType, UtilsLD.Json)
    fhr.headers().set(UtilsLD.ContentType2, UtilsLD.Json)
    fhr.headers().set(UtilsLD.Accept, UtilsLD.Json)
    fhr.headers().set(UtilsLD.UserAgent, UtilsLD.Orion)
    fhr.headers().set(UtilsLD.FiwareService, UtilsLD.Demo)
    fhr.headers().set(UtilsLD.FiwareServicePath, UtilsLD.Test)
    fhr
  }

  @Test def correctNotification: Unit = {
    val sc  =  new DummySourceContextLD()
    val ohh = new OrionHttpHandlerLD(sc)
    val req = createMockFullHttpRequest()
    val mockCtx = mock(classOf[ChannelHandlerContext])
  //  ohh.channelRead(mockCtx, req)
    var res = ohh.parseMessage(req)
    Assert.assertTrue(res.isInstanceOf[NgsiEventLD])

  }

  @Test
  def incorrectNotification: Unit = {
    val sc  =  new DummySourceContextLD()
    val ohh = new OrionHttpHandlerLD(sc)
    val req = createMockFullHttpRequest("{}")
    val mockCtx = mock(classOf[ChannelHandlerContext])
    //  ohh.channelRead(mockCtx, req)
    var res = ohh.parseMessage(req)
    Assert.assertNull(res)
  }

  @Test(expected=classOf[java.lang.Exception])
  def getNotification: Unit = {
    val sc  =  new DummySourceContextLD()
    val ohh = new OrionHttpHandlerLD(sc)
    val req = createMockFullHttpRequestGet()
    val mockCtx = mock(classOf[ChannelHandlerContext])
    ohh.channelRead(mockCtx, req)
  }

  @Test def postNotification: Unit = {
    val sc  =  new DummySourceContextLD()
    val ohh = new OrionHttpHandlerLD(sc)
    val req = createMockFullHttpRequest()
    val mockCtx = mock(classOf[ChannelHandlerContext])
    ohh.channelRead(mockCtx, req)

  }

  @Test def buildHttpPostSinkEntity : Unit = {
    val os = OrionSinkObject(UtilsLD.Content, UtilsLD.OrionAddress, ContentType.Plain, HTTPMethod.POST)
    val httpMsg = OrionSink.createHttpMsg(os)
    val content = scala.io.Source.fromInputStream(httpMsg.getEntity.getContent).mkString

    Assert.assertEquals(httpMsg.getHeaders(UtilsLD.ContentType)(0).getValue, ContentType.Plain.toString())
    Assert.assertEquals(httpMsg.getMethod(), "POST")
    Assert.assertEquals(content, UtilsLD.Content)
  }

  @Test def buildHttpPutSinkEntity : Unit = {
    val os = OrionSinkObject(UtilsLD.Content, UtilsLD.OrionAddress, ContentType.JSON, HTTPMethod.PUT)
    val httpMsg = OrionSink.createHttpMsg(os)
    val content = scala.io.Source.fromInputStream(httpMsg.getEntity.getContent).mkString

    Assert.assertEquals(httpMsg.getHeaders(UtilsLD.ContentType)(0).getValue, ContentType.JSON.toString())
    Assert.assertEquals(httpMsg.getMethod(), "PUT")
    Assert.assertEquals(content, UtilsLD.Content)
  }

  @Test def buildHttpPatchSinkEntity : Unit = {
    val os = OrionSinkObject(UtilsLD.Content, UtilsLD.OrionAddress, ContentType.JSON, HTTPMethod.PATCH)
    val httpMsg = OrionSink.createHttpMsg(os)
    val content = scala.io.Source.fromInputStream(httpMsg.getEntity.getContent).mkString

    Assert.assertEquals(httpMsg.getHeaders(UtilsLD.ContentType)(0).getValue, ContentType.JSON.toString())
    Assert.assertEquals(httpMsg.getMethod(), "PATCH")
    Assert.assertEquals(content, UtilsLD.Content)
  }

  @Test def getHTTPMethod : Unit = {
   Assert.assertTrue(OrionSink.getMethod(HTTPMethod.POST,"").isInstanceOf[HttpPost])
   Assert.assertTrue(OrionSink.getMethod(HTTPMethod.PUT,"").isInstanceOf[HttpPut])
   Assert.assertTrue(OrionSink.getMethod(HTTPMethod.PATCH,"").isInstanceOf[HttpPatch])
  }
  @Test (expected=classOf[java.lang.Exception]) def nettyServerCallbackUrl : Unit = {
    val sc  =  new DummySourceContextLD()
    val os = new OrionHttpServerLD(sc)
    Assert.assertEquals(os.startNettyServer(UtilsLD.Port,Some("http://callback")).getPort(),UtilsLD.Port)
  }
  @Test def nettyServerNoCallbackUrl : Unit = {
    val sc  =  new DummySourceContextLD()
    val os : OrionHttpServerLD = new OrionHttpServerLD(sc)
    new Thread(new Runnable {
      def run() {
        Thread.sleep(UtilsLD.SleepTime)
        os.close()
      }
    }).run()


    var  currentAddr : InetSocketAddress = os.startNettyServer(UtilsLD.Port,None)
    Assert.assertEquals(currentAddr.getPort(), UtilsLD.Port)
  }

  @Test def orionSource() : Unit = {
    run(() =>FlinkJobTest.main(Array()))
    Thread.sleep(UtilsLD.SleepTime*2)
    for ( x <- 0 to 10){
      val json = simulatedNotificationLD.notification(10*x,x).toString
      sendPostRequest(UtilsLD.OtherUrl,json)
      Thread.sleep(UtilsLD.SleepTimeShort*2)
    }
    Thread.sleep(UtilsLD.SleepTimeShort)
    Assert.assertEquals(simulatedNotificationLD.maxTempVal,100*1,0)
    Assert.assertEquals(simulatedNotificationLD.maxPresVal,10*1,0)
  }

  @Test def orionSourceBadRequest() : Unit = {
    run(() =>FlinkJobTest.main(Array()))
    Thread.sleep(UtilsLD.SleepTime)
    val originalValue = simulatedNotificationLD.maxTempVal

    for ( x <- 0 to 10){
      sendPostRequest(UtilsLD.OtherUrl,UtilsLD.BadContent)
      Thread.sleep(UtilsLD.SleepTimeShort)
    }
    Thread.sleep(UtilsLD.SleepTimeShort)
    Assert.assertEquals(simulatedNotificationLD.maxTempVal,originalValue,0)

  }
}
