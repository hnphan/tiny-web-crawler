package com.hieuphan.tinywebscrawler

import org.jsoup.Jsoup

import scala.collection.mutable.Set
import scala.collection.mutable.Queue
import scala.collection.JavaConverters._
import java.net.URL

import com.typesafe.scalalogging.LazyLogging
import scalax.collection.Graph
import scalax.collection.GraphPredef._
import scalax.collection.GraphEdge._


object SingleThreadCrawler extends LazyLogging {

  def crawl(startUrl: String, maxDepth: Int): Graph[String, DiEdge] = {
    logger.info(s"Will start crawling from ${startUrl} with maximum depth of ${maxDepth}")

    var currentDepth = 0
    var siteMapGraph = Graph[String, DiEdge]()
    siteMapGraph = siteMapGraph + startUrl


    val visitedUrls: Set[String] = Set(startUrl)
    val toVisitUrls: Queue[String] = Queue()

    val childrenUrls = getChildrenUrls(startUrl)
    childrenUrls.foreach(childUrl => siteMapGraph + startUrl ~> childUrl)

    childrenUrls.foreach(toVisitUrls.enqueue(_))

    while (!toVisitUrls.isEmpty && currentDepth < maxDepth) {
      currentDepth += 1
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
    if (!url.startsWith("http://") && !url.startsWith("https://")) {
      throw new IllegalArgumentException("URL needs to start with http:// or https://")
    }

    val baseDomain = new URL(url).getHost
    val doc = Jsoup.connect(url)
      .userAgent("Not a robot")
      .get()

    val foo = Jsoup.parse(doc.html(), url).select("a[href]").iterator().asScala.map(_.absUrl("href")).toSeq
    foo
      .filter(l => l != null && !l.isEmpty)
      .filter(l => !l.contains("mailto"))
      .filter(l => new URL(l).getHost == baseDomain)
  }


  def main(args: Array[String]) = {
    case class Config(startUrl: String = "https://www.google.co.uk", maxDepth: Int = 2)

    val parser = new scopt.OptionParser[Config]("single-thread-crawler") {
      head("Welcome to tiny web crawler!", "0.1")

      opt[String]('u', "url").required().valueName("<startUrl>").action((s, c) => c.copy(startUrl = s))
      opt[Int]('d', "d")
        .required()
        .valueName("<maxDepth>")
        .withFallback(() => 5)
        .validate(depth => {
          if (depth > 0) success
          else failure("Value <maxDepth> must be greater than zero")
        })
        .action((d, config) => config.copy(maxDepth = d))
    }

    parser.parse(args, Config()) match {
      case Some(config) =>
        // do stuff
        val map = crawl(config.startUrl, config.maxDepth)
        logger.info(s"Site map: \n${map.edges.asSortedString("\n").replace("~>", " ~> ")}")
      case None =>
        // arguments are bad, error message will have been displayed
        logger.error("One or more of the supplied arguments are not valid.")
    }
  }
}



