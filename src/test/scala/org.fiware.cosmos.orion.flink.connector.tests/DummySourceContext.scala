package org.fiware.cosmos.orion.flink.connector.tests

import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.watermark.Watermark
import java.util

import org.fiware.cosmos.orion.flink.connector.NgsiEvent

 object DummySourceContext {
  private val lock : Object =  None
  private var numElementsCollected = 0L
  private var message   = scala.collection.mutable.ArrayBuffer.empty[NgsiEvent]
}
class DummySourceContext() extends SourceFunction.SourceContext[NgsiEvent] {
  DummySourceContext.numElementsCollected = 0
  DummySourceContext.message  = scala.collection.mutable.ArrayBuffer.empty[NgsiEvent]

  override def collect(element: NgsiEvent): Unit = {
    DummySourceContext.message += element
    DummySourceContext.numElementsCollected += 1
  }

  override def collectWithTimestamp(element: NgsiEvent, timestamp: Long): Unit = {
    DummySourceContext.message+=element
    DummySourceContext.numElementsCollected += 1
  }

  override def emitWatermark(mark: Watermark): Unit = {
  }

  override def markAsTemporarilyIdle(): Unit = {
    throw new UnsupportedOperationException
  }

  override def getCheckpointLock: Object = DummySourceContext.lock

  override def close(): Unit = {
  }
}
