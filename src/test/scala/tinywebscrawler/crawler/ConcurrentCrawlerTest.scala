package tinywebscrawler.crawler

import java.net.URL

import com.hieuphan.tinywebscrawler.crawler.ConcurrentCrawler
import com.hieuphan.tinywebscrawler.scraper.{RetryableInternalLinksScraper, SimpleWebResource}
import org.specs2.mock.Mockito
import org.specs2.mutable.{After, Specification}
import org.specs2.specification.Scope

class ConcurrentCrawlerTest extends Specification with Mockito {

  "ConcurrentCrawler" should {
    "crawl all internal links and cope with cyclic routes" in new MockedEnv {
      mockScraper.scrape(startUrl) returns homepage
      mockScraper.scrape(subpage1Url) returns subpage1
      mockScraper.scrape(subpage2Url) returns subpage2
      mockScraper.scrape(subpage3Url) returns subpage3

      val crawledContent = crawler.crawl(startUrl)
      crawledContent must containTheSameElementsAs(Seq(homepage, subpage1, subpage2, subpage3))
    }

    "crawl only to the specified depth" in new MockedEnv {
      mockScraper.scrape(startUrl) returns homepage
      mockScraper.scrape(subpage1Url) returns subpage1
      mockScraper.scrape(subpage2Url) returns subpage2
      mockScraper.scrape(subpage2Url) returns subpage2

      val crawledContent = crawler.crawl(startUrl, maxDepth = 2) // should not get to subpage3
      crawledContent must containTheSameElementsAs(Seq(homepage, subpage1, subpage2))
    }

    "shutdown executor when instructed" in new MockedEnv {
      crawler.shutdown()
      crawler.fixedThreadPoolExecutor.isShutdown must beTrue
    }

  }

  trait MockedEnv extends Scope with After {
    val startUrl = new URL("http://www.example.com")
    val subpage1Url = new URL("http://www.example.com/subpage1")
    val subpage2Url = new URL("http://www.example.com/subpage2")
    val subpage3Url = new  URL("http://www.example.com/subpage1/subpage3/")

    val homepage = SimpleWebResource(startUrl, Set(subpage1Url, subpage2Url))
    val subpage1 = SimpleWebResource(subpage1Url, Set(startUrl, subpage2Url, subpage3Url))
    val subpage2 = SimpleWebResource(subpage2Url, Set())
    val subpage3 = SimpleWebResource(subpage3Url, Set())


    val mockScraper = mock[RetryableInternalLinksScraper]
    val crawler = new ConcurrentCrawler(mockScraper, maxConcurrency = 8)

    def after = crawler.shutdown()
  }
}
