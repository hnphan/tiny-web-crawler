package com.hieuphan.tinywebscrawler.util

import com.typesafe.scalalogging.LazyLogging

trait ExecutionTimeLogger extends LazyLogging {
  def executeAndLogExecutionTime[R](block: => R): R = {
    val t0 = System.nanoTime()
    val result = block
    val t1 = System.nanoTime()
    logger.info("Done. Elapsed time: " + (t1 - t0)/1000000000.0 + "s")
    result
  }
}
