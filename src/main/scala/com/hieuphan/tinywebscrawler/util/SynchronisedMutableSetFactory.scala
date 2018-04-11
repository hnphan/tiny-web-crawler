package com.hieuphan.tinywebscrawler.util

trait SynchronisedMutableSetFactory {
  def newSynchronisedMutableSet[T]() = {
    import scala.collection.JavaConverters._
    java.util.Collections.newSetFromMap(
      new java.util.concurrent.ConcurrentHashMap[T, java.lang.Boolean]).asScala
  }
}
