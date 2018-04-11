package com.hieuphan.tinywebscrawler.scraper

import java.net.URL

import com.hieuphan.tinywebscrawler.webclient.{RetryableJsoupWebClient, WebClientException}
import com.typesafe.scalalogging.LazyLogging
import org.jsoup.Jsoup

import scala.collection.JavaConverters._

case class RetryableInternalLinksScraper(webClient: RetryableJsoupWebClient) extends Scraper with LazyLogging {

  def scrape(url: URL): SimpleWebResource = {
    try {
      val response = webClient.get(url)
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
      case e: WebClientException => {
        logger.warn(s"Oops, something bad happened when trying to visit ${url.toString}.")
        SimpleWebResource(url, Set())
      }
    }
  }

}
