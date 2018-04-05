package com.hieuphan.tinywebscrawler

import org.jsoup.Jsoup

import scala.collection.mutable.Set
import scala.collection.mutable.Queue
import scala.collection.JavaConverters._
import java.net.URL

import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._


object SingleThreadCrawler {

  def crawl(startUrl: String): Graph[String, DiEdge] = {
    var limit = 10
    var siteMapGraph = Graph[String, DiEdge]()
    siteMapGraph = siteMapGraph + startUrl


    val visitedUrls: Set[String] = Set(startUrl)
    val toVisitUrls: Queue[String] = Queue()

    println(s"Starting to crawl from ${startUrl}")

    val childrenUrls = getChildrenUrls(startUrl)
    childrenUrls.foreach(childUrl => siteMapGraph + startUrl ~> childUrl)

    childrenUrls.foreach(toVisitUrls.enqueue(_))

    while (!toVisitUrls.isEmpty && limit > 0) {
      limit = limit - 1
      val nextUrl = toVisitUrls.dequeue()
      if (!visitedUrls.contains(nextUrl)) {
        visitedUrls.add(nextUrl)
        getChildrenUrls(nextUrl).foreach(childUrl => {
          toVisitUrls.enqueue(childUrl)
          siteMapGraph = siteMapGraph + nextUrl ~> childUrl
        })
      }
    }
    siteMapGraph
  }

  def getChildrenUrls(url: String): Seq[String] = {
    val baseDomain = new URL(url).getHost
    val document = Jsoup.connect(url)
      .userAgent("Not a robot")

    val foo = Jsoup.parse(document.get().html(), url).select("a[href]").iterator().asScala.map(_.absUrl("href")).toSeq
    foo
      .filter(l => l != null && !l.isEmpty)
      .filter(l => !l.contains("mailto"))
      .filter(l => new URL(l).getHost == baseDomain)
  }
}
object Main extends App  {
  val map = SingleThreadCrawler.crawl("https://monzo.com")

  println(map.edges.asSortedString("\n").replace("~>", " ~> ") )
}

