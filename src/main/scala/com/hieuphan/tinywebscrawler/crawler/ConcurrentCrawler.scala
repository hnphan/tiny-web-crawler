package com.hieuphan.tinywebscrawler.crawler

import java.net.URL
import java.util.concurrent.Executors

import com.hieuphan.tinywebscrawler.scraper.{Scraper, WebResource}
import com.hieuphan.tinywebscrawler.util.SynchronisedMutableSetFactory
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, ExecutionContext, Future, TimeoutException}

class ConcurrentCrawler(scraper: Scraper, maxConcurrency: Int = 8) extends Crawler
  with LazyLogging with SynchronisedMutableSetFactory {

  val fixedThreadPoolExecutor = Executors.newFixedThreadPool(maxConcurrency)
  implicit val ec = ExecutionContext.fromExecutor(fixedThreadPoolExecutor)

  def crawl(startUrl: URL, maxDepth: Int) = {
    logger.info(s"Will start crawling from ${startUrl} using maxConcurrency ${maxConcurrency}")
    var currentDepth = 0

    var scrapedUrls = scala.collection.mutable.Set[URL]()

    val crawledContent = newSynchronisedMutableSet[WebResource]()
    var urlsToScrape = scala.collection.mutable.Set[URL](startUrl)

    try {
      while (urlsToScrape.nonEmpty && currentDepth < maxDepth) {
        logger.debug(s"Crawling at depth ${currentDepth}")
        val visitThisDepth = Future.sequence(urlsToScrape
          .map(url => {
            Future(scraper.scrape(url)).map(webContent => {
              crawledContent.add(webContent)
              webContent
            })
          })
        )
        // blocking before the next depth/give up if time out
        // assuming that it shouldn't take longer than 10 seconds to scrape per link
        // should probably make this configurable
        val urlsInNextDepth = Await.result(visitThisDepth, Duration(s"${urlsToScrape.size * 10}s")).map(_.links).flatten
        scrapedUrls ++= urlsToScrape
        urlsToScrape.clear
        urlsToScrape ++= urlsInNextDepth.filter(!scrapedUrls.contains(_))
        currentDepth+=1
      }
    }
    catch {
      case _: TimeoutException => logger.error("Timed out while crawling. Will return what we found so far.")
      case _: Throwable => logger.error(s"An error has occured while crawling. Will return what we found so far.")
    }
    crawledContent
  }

  def shutdown() = fixedThreadPoolExecutor.shutdown()

}
