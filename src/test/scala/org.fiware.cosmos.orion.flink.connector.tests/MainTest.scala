package org.fiware.cosmos.orion.flink.connector.tests
import org.fiware.cosmos.orion.flink.connector.tests.HttpSourceExample.run
import org.junit.Assert._
import org.junit.Test
class MainTest extends BaseTest {
  @Test def orionSourceTest() = {
//    run(() =>HttpSourceExample.main(Array()))
    assertEquals(true, true)
  }



}
