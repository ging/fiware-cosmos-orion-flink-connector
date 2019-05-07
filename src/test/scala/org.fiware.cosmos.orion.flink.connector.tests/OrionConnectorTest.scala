package org.fiware.cosmos.orion.flink.connector.tests

import java.net.{InetAddress, InetSocketAddress}

import io.netty.handler.codec.http.{DefaultFullHttpRequest, HttpMethod, HttpVersion}
import org.fiware.cosmos.orion.flink.connector._
import org.junit.{Assert, Test}
import io.netty.buffer.Unpooled
import io.netty.channel.ChannelHandlerContext
import io.netty.util.CharsetUtil
import org.apache.http.client.methods.{HttpPatch, HttpPost, HttpPut}
import org.fiware.cosmos.orion.flink.connector.test.FlinkJobTest
import org.mockito.Mockito.mock
class OrionConnectorTest extends  BaseTest{
  def createMockFullHttpRequest(str: String = SimulatedNotification.notification()): DefaultFullHttpRequest ={
    val bytes = str.getBytes(CharsetUtil.UTF_8)
    val content = Unpooled.copiedBuffer(bytes)
    val fhr = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.POST, "http://localhost:9001", content)
    fhr.headers().set("Content-Type", "application/json; charset=utf-8")
    fhr.headers().set("Content-Type2", "application/json; charset=utf-8")
    fhr.headers().set("Accept", "application/json")
    fhr.headers().set("User-Agent", "orion/0.10.0")
    fhr.headers().set("Fiware-Service", "demo")
    fhr.headers().set("Fiware-ServicePath", "/test")
    fhr
  }

  def createMockFullHttpRequestGet(): DefaultFullHttpRequest ={
    val fhr = new DefaultFullHttpRequest(HttpVersion.HTTP_1_1, HttpMethod.GET, "http://localhost:9001")
    //  val headers = new HttpHeaders("Content-Type", "application/json; charset=utf-8")
    fhr.headers().set("Content-Type", "application/json; charset=utf-8")
    fhr.headers().set("Content-Type2", "application/json; charset=utf-8")
    fhr.headers().set("Accept", "application/json")
    fhr.headers().set("User-Agent", "orion/0.10.0")
    fhr.headers().set("Fiware-Service", "demo")
    fhr.headers().set("Fiware-ServicePath", "/test")
    fhr
  }

  @Test def correctNotification: Unit = {
    val sc  =  new DummySourceContext()
    val ohh = new OrionHttpHandler(sc)
    val req = createMockFullHttpRequest()
    val mockCtx = mock(classOf[ChannelHandlerContext])
  //  ohh.channelRead(mockCtx, req)
    var res = ohh.parseMessage(req)
    Assert.assertTrue(res.isInstanceOf[NgsiEvent])

  }

  @Test(expected=classOf[org.json4s.MappingException])
  def incorrectNotification: Unit = {
    val sc  =  new DummySourceContext()
    val ohh = new OrionHttpHandler(sc)
    val req = createMockFullHttpRequest("{}")
    val mockCtx = mock(classOf[ChannelHandlerContext])
    //  ohh.channelRead(mockCtx, req)
    var res = ohh.parseMessage(req)
  }

  @Test(expected=classOf[java.lang.Exception])
  def getNotification: Unit = {
    val sc  =  new DummySourceContext()
    val ohh = new OrionHttpHandler(sc)
    val req = createMockFullHttpRequestGet()
    val mockCtx = mock(classOf[ChannelHandlerContext])
    ohh.channelRead(mockCtx, req)
  }

  @Test def postNotification: Unit = {
    val sc  =  new DummySourceContext()
    val ohh = new OrionHttpHandler(sc)
    val req = createMockFullHttpRequest()
    val mockCtx = mock(classOf[ChannelHandlerContext])
    ohh.channelRead(mockCtx, req)

  }

  @Test def buildHttpPostSinkEntity : Unit = {
    val os = new OrionSinkObject("Content", "http://localhost:2026",  ContentType.Plain, HTTPMethod.POST)
    val httpMsg = OrionSink.createHttpMsg(os)
    val content = scala.io.Source.fromInputStream(httpMsg.getEntity.getContent).mkString

    Assert.assertEquals(httpMsg.getHeaders("Content-type")(0).getValue, ContentType.Plain.toString())
    Assert.assertEquals(httpMsg.getMethod(), "POST")
    Assert.assertEquals(content, "Content")
  }

  @Test def buildHttpPutSinkEntity : Unit = {
    val os = new OrionSinkObject("Content", "http://localhost:2026",  ContentType.JSON, HTTPMethod.PUT)
    val httpMsg = OrionSink.createHttpMsg(os)
    val content = scala.io.Source.fromInputStream(httpMsg.getEntity.getContent).mkString

    Assert.assertEquals(httpMsg.getHeaders("Content-type")(0).getValue, ContentType.JSON.toString())
    Assert.assertEquals(httpMsg.getMethod(), "PUT")
    Assert.assertEquals(content, "Content")
  }

  @Test def buildHttpPatchSinkEntity : Unit = {
    val os = new OrionSinkObject("Content", "http://localhost:2026",  ContentType.JSON, HTTPMethod.PATCH)
    val httpMsg = OrionSink.createHttpMsg(os)
    val content = scala.io.Source.fromInputStream(httpMsg.getEntity.getContent).mkString

    Assert.assertEquals(httpMsg.getHeaders("Content-type")(0).getValue, ContentType.JSON.toString())
    Assert.assertEquals(httpMsg.getMethod(), "PATCH")
    Assert.assertEquals(content, "Content")
  }

  @Test def getHTTPMethod : Unit = {
   Assert.assertTrue(OrionSink.getMethod(HTTPMethod.POST,"").isInstanceOf[HttpPost])
   Assert.assertTrue(OrionSink.getMethod(HTTPMethod.PUT,"").isInstanceOf[HttpPut])
   Assert.assertTrue(OrionSink.getMethod(HTTPMethod.PATCH,"").isInstanceOf[HttpPatch])
  }

  @Test (expected=classOf[java.lang.Exception]) def nettyServerCallbackUrl : Unit = {
    val sc  =  new DummySourceContext()
    val os = new OrionHttpServer(sc)
    Assert.assertEquals(os.startNettyServer(9001,Some("http://callback")).getPort(),9001)
  }
  @Test def nettyServerNoCallbackUrl : Unit = {
    val sc  =  new DummySourceContext()
    val os : OrionHttpServer = new OrionHttpServer(sc)
    new Thread(new Runnable {
      def run() {
        Thread.sleep(10000)
        os.close()
      }
    }).run()


    var  currentAddr : InetSocketAddress = os.startNettyServer(9001,None)
    Assert.assertTrue(currentAddr.isInstanceOf[InetSocketAddress])
    Assert.assertEquals(currentAddr.getPort(),9001)
  }

  @Test (expected= classOf[java.util.concurrent.RejectedExecutionException]) def nettyServerCollide : Unit = {
    val sc  =  new DummySourceContext()
    val sc2  =  new DummySourceContext()
    val os : OrionHttpServer = new OrionHttpServer(sc)
    val os2 : OrionHttpServer = new OrionHttpServer(sc)
    new Thread(new Runnable {
      def run() {
        Thread.sleep(10000)
        os.close()
      }
    }).run()
    new Thread(new Runnable {
      def run() {
        Thread.sleep(10000)
        os2.close()
      }
    }).run()

    Assert.assertTrue(true)
    var  currentAddr : InetSocketAddress = os.startNettyServer(9001,None)
    var  currentAddr2 : InetSocketAddress = os2.startNettyServer(9001,None)
  }
  @Test def orionSource() : Unit = {
    run(() =>FlinkJobTest.main(Array()))
    Thread.sleep(10000)
    for ( x <- 0 to 10){
      val url = "http://localhost:9102"
      val json = SimulatedNotification.notification(10*x,x).toString
      sendPostRequest(url,json)
      Thread.sleep(1000)
    }
    Thread.sleep(2000)
    Assert.assertEquals(SimulatedNotification.maxTempVal,100,0)
    Assert.assertEquals(SimulatedNotification.maxPresVal,10,0)
  }

  @Test def orionSourceBadRequest() : Unit = {
    run(() =>FlinkJobTest.main(Array()))
    Thread.sleep(10000)
    val originalValue = SimulatedNotification.maxTempVal

    for ( x <- 0 to 10){
      val url = "http://localhost:9102"
      val json = "BAD CONTENT".toString
      sendPostRequest(url,json)
      Thread.sleep(1000)
    }
    Thread.sleep(3000)
    Assert.assertEquals(SimulatedNotification.maxTempVal,originalValue,0)


  }
}
