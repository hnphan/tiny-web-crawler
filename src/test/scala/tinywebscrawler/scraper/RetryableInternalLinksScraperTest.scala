package tinywebscrawler.scraper

import java.io.File

import com.hieuphan.tinywebscrawler.scraper.RetryableInternalLinksScraper
import org.specs2.mutable.Specification

class RetryableInternalLinksScraperTest extends Specification {
  "RetryableInternalLinksScraper" should {
    "scrape only internal links from a web page" in {
      val scraper = new RetryableInternalLinksScraper()
      val url = new File("./src/test/resources/html/reddit.html").toURI().toURL()
      val result = scraper.scrape(url)
      result.links.size must_== 0
    }

    "retry when encoutering retryable exceptions" in {
      success
    }

    "not retry when encoutering unretryable exceptions (such as 404)" in {
      success
    }

    "back out expontentially when retrying" in {
      success
    }

    "return a WebResource object with an empty list of links when permanently fails" in {
      success
    }
  }
}
