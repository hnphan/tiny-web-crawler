package tinywebscrawler.scraper

import java.net.URL

import com.hieuphan.tinywebscrawler.scraper.RetryableInternalLinksScraper
import com.hieuphan.tinywebscrawler.webclient.{RetryableJsoupWebClient, WebClientException}
import org.jsoup.Connection.Response
import org.specs2.mock.Mockito
import org.specs2.mutable.Specification
import org.specs2.specification.Scope
import org.jsoup.nodes.Document

class RetryableInternalLinksScraperTest extends Specification with Mockito {

  "RetryableInternalLinksScraper" should {
    "scrape only internal links from a web page" in new MockedEnv {
      val scraper = new RetryableInternalLinksScraper(mockWebClient)
      mockWebClient.get(testURL) returns mockOkResponse
      mockOkResponse.contentType returns htmlContentType
      mockOkResponse.parse() returns mockDocument
      mockDocument.html returns htmlString
      val result = scraper.scrape(testURL)

      result.url must_== testURL
      result.links.size must_== 2
      result.links.exists(l => l.getHost != testURL.getHost) must beFalse
    }

    "return a WebResource object with an empty list of links when permanently fails" in new MockedEnv {
      val scraper = new RetryableInternalLinksScraper(mockWebClient)
      mockWebClient.get(testURL) throws exception

      val result = scraper.scrape(testURL)

      result.url must_== testURL
      result.links.size must_== 0
    }
  }

  trait MockedEnv extends Scope {
    val mockWebClient = mock[RetryableJsoupWebClient]
    val mockOkResponse = mock[Response]
    val mockDocument = mock[Document]
    val exception = WebClientException(new Exception("Oops!"))
    val testURL = new URL("https://www.example.com")
    val htmlContentType = "text/html"
    val htmlString =
      s"""
         |<!DOCTYPE html>
         |    <html lang="en">
         |      <head>
         |        <meta charset="UTF-8">
         |        <title>Title</title>
         |      </head>
         |      <body>
         |          <a href="https://www.example.com/an-internal-page/">Click here for cat pics</a>
         |          <a href="https://www.example.com/another-internal-page/">You won't believe what you'll see</a>
         |          <a href="https://www.clickbait.com/">Congratulations! You've won!!!</a>
         |       </body>
         |    </html>
         |
       """.stripMargin

  }

}
