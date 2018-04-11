package com.hieuphan.tinywebscrawler

import java.net.URL

import com.hieuphan.tinywebscrawler.crawler.ConcurrentCrawler
import com.hieuphan.tinywebscrawler.scraper.RetryableInternalLinksScraper
import com.hieuphan.tinywebscrawler.util.ExecutionTimeUtil
import com.hieuphan.tinywebscrawler.webclient.{JsoupWebClient, RetryableJsoupWebClient}

case class Config(startUrl: String = "https://www.google.co.uk", maxDepth: Int = Int.MaxValue, maxConcurrency: Int = 8)

object Main extends App with ExecutionTimeUtil {

  val parser = new scopt.OptionParser[Config]("tiny-thread-crawler") {
    head("Welcome to tiny web crawler!", "0.1")

    opt[String]('u', "url").required().valueName("<startUrl>").action((s, c) => c.copy(startUrl = s))

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
  }

  parser.parse(args, Config()) match {
    case Some(config) =>
      val webClient = RetryableJsoupWebClient(maxAttempts = 3)
      val crawler = new ConcurrentCrawler(RetryableInternalLinksScraper(webClient), config.maxConcurrency)
      val startUrl = new URL(config.startUrl)

      val crawledContent = executeAndLogExecutionTimeInSeconds(crawler.crawl(startUrl, config.maxDepth))

      crawledContent.foreach(
        content => logger.info(s"\nURL: ${content.url.toString}\nLinks:\n${content.links.mkString("\t","\n\t","")} ")
      )

      crawler.shutdown()
    case None =>
      logger.error("One or more of the supplied arguments are not valid. Program will exit.")
  }
}
