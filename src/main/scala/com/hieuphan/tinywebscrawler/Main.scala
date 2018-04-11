package com.hieuphan.tinywebscrawler

import java.net.URL

import com.hieuphan.tinywebscrawler.crawler.ConcurrentCrawler
import com.hieuphan.tinywebscrawler.scraper.RetryableInternalLinksScraper
import com.hieuphan.tinywebscrawler.util.ExecutionTimeUtil
import com.hieuphan.tinywebscrawler.webclient.RetryableJsoupWebClient
import org.apache.commons.validator.routines.UrlValidator

case class Config(startUrl: String = "https://www.google.co.uk", maxDepth: Int = Int.MaxValue, maxConcurrency: Int = 8)

object Main extends App with ExecutionTimeUtil {

  val parser = new scopt.OptionParser[Config]("tiny-thread-crawler") {
    head("Welcome to tiny web crawler", "0.1")

    def isUrlValid(url: String) = {
      val validator = new UrlValidator(Array("http", "https"))
      validator.isValid(url)
    }

    opt[String]('u', "url").required().valueName("<startUrl>")
      .validate(url => {
        if (isUrlValid(url)) success
        else failure("Value <startUrl> must be a http:// or https://")
      })
      .action((s, c) => c.copy(startUrl = s))

    opt[Int]('d', "depth")
      .optional()
      .valueName("<maxDepth>")
      .validate(depth => {
        if (depth > 0) success
        else failure("Value <maxDepth> must be greater than zero.")
      })
      .action((d, config) => config.copy(maxDepth = d))

    opt[Int]('c', "concurrency")
      .optional()
      .valueName("<maxConcurrency>")
      .validate(maxConcurrency => {
        if (maxConcurrency > 0) success
        else failure("Value <maxConcurrency> must be greater than zero.")
      })
      .action((maxConcurrency, config) => config.copy(maxConcurrency = maxConcurrency))

    help("help").text("prints this usage text")
  }

  parser.parse(args, Config()) match {
    case Some(config) =>
      val webClient = RetryableJsoupWebClient(maxAttempts = 3)
      val crawler = new ConcurrentCrawler(RetryableInternalLinksScraper(webClient), config.maxConcurrency)
      val startUrl = new URL(config.startUrl)

      val crawledContent = executeAndLogExecutionTimeInSeconds(crawler.crawl(startUrl, config.maxDepth))
      logger.info(
        crawledContent.toSeq
          .sortWith((left, right) => (left.url.toString.compareTo(right.url.toString) < 0))
          .map(content => s"\n${content.url.toString}\n${content.links.mkString("\t\t","\n\t\t","")} ")
            .mkString("\n"))
      crawler.shutdown()
    case None =>
      logger.error("One or more of the supplied arguments are not valid. Program will exit.")
  }
}
