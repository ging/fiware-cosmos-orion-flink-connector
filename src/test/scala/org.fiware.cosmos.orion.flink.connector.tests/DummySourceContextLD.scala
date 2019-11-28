package org.fiware.cosmos.orion.flink.connector.tests

import org.apache.flink.streaming.api.functions.source.SourceFunction
import org.apache.flink.streaming.api.watermark.Watermark
import org.fiware.cosmos.orion.flink.connector.NgsiEventLD

object DummySourceContextLD {
  private val lock : Object =  None
  private var numElementsCollected = 0L
  private var message   = scala.collection.mutable.ArrayBuffer.empty[NgsiEventLD]
}

class DummySourceContextLD() extends SourceFunction.SourceContext[NgsiEventLD] {
  DummySourceContextLD.numElementsCollected = 0
  DummySourceContextLD.message  = scala.collection.mutable.ArrayBuffer.empty[NgsiEventLD]

  override def collect(element: NgsiEventLD): Unit = {
    DummySourceContextLD.message += element
    DummySourceContextLD.numElementsCollected += 1
  }

  override def collectWithTimestamp(element: NgsiEventLD, timestamp: Long): Unit = {
    DummySourceContextLD.message+=element
    DummySourceContextLD.numElementsCollected += 1
  }

  override def emitWatermark(mark: Watermark): Unit = {
  }

  override def markAsTemporarilyIdle(): Unit = {
    throw new UnsupportedOperationException
  }

  override def getCheckpointLock: Object = DummySourceContextLD.lock

  override def close(): Unit = {
  }
}
