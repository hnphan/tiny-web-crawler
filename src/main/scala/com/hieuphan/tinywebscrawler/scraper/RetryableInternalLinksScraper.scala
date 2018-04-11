package com.hieuphan.tinywebscrawler.scraper

import java.net.{SocketTimeoutException, URL}

import com.typesafe.scalalogging.LazyLogging
import org.jsoup.{HttpStatusException, Jsoup}

import scala.collection.JavaConverters._
import scala.math.pow

class RetryableInternalLinksScraper(maxAttempts: Int = 3, ) extends Scraper with LazyLogging {
  require(maxAttempts > 1)

  def scrape(url: URL) = scrape(url, attemptsSoFar = 0)

  def scrape(url: URL, attemptsSoFar: Int = 0): SimpleWebResource = {
    try {
      val response = Jsoup.connect(url.toString).ignoreContentType(true)
        .userAgent("Totally not a bot")
        .execute()

      val contentType: String = response.contentType
      if (contentType.startsWith("text/html")) {
        val doc = response.parse()
        val links: Set[URL] = Jsoup.parse(doc.html(), url.toString)
          .select("a[href]")
          .iterator().asScala
          .map(_.absUrl("href"))
          .filter(l => l != null && !l.isEmpty)
          .filter(l => !l.contains("mailto"))
          .map(link => new URL(link))
          .filter(l => l.getHost == url.getHost)
          .toSet
        SimpleWebResource(url, links)
      } else {
        // in case the url is not html, for example, an image or a pdf file
        SimpleWebResource(url, Set())
      }
    }
    catch {
      case e: HttpStatusException =>
        if (isExceptionRetryable(e) && attemptsSoFar < maxAttempts) {
          Thread.sleep(1000 * pow(2, attemptsSoFar).toLong) // exponential back off
          this.scrape(url, attemptsSoFar + 1)
        }
        else {
          logger.warn(s"Couldn't scrape ${url}. Will return empty content.")
          SimpleWebResource(url, Set())
        }
      case _: SocketTimeoutException =>
        if (attemptsSoFar < maxAttempts) {
          Thread.sleep(1000 * pow(2, attemptsSoFar).toLong) // exponential back off
          this.scrape(url, attemptsSoFar + 1)
        }
        else {
          logger.warn(s"Couldn't scrape ${url}. Will return empty content.")
          SimpleWebResource(url, Set())
        }
      case _: Throwable =>
        logger.warn(s"Couldn't scrape ${url}. Will return empty content.")
        SimpleWebResource(url, Set())
    }
  }

  private def isExceptionRetryable(e: HttpStatusException): Boolean = {
    // maybe more status codes can be considered retryable?
    return e.getStatusCode == 503
  }

}
