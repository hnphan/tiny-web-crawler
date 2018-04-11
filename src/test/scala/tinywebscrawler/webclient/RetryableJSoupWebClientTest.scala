package tinywebscrawler.webclient

import java.net.URL

import com.hieuphan.tinywebscrawler.webclient.{JSoupWebClient, RetryableJSoupWebClient, WebClientException}
import org.specs2.mutable.Specification
import org.specs2.mock.Mockito
import org.specs2.specification.Scope
import org.jsoup.Connection.Response
import org.jsoup.HttpStatusException

import scala.concurrent.ExecutionContext.Implicits.global

class RetryableJSoupWebClientTest extends Specification with Mockito {

  "RetryableJSoupWebClient" should {
    "issue request and get response successfully" in new MockedEnv {
      val retryableWebClient = new RetryableJSoupWebClient(maxAttempts = 3, mockWebClient)
      mockWebClient.getResponse(testUrl) returns okResponse

      val response = retryableWebClient.get(testUrl)
      response must_== okResponse
    }

    "retry according to specified maxAttempts if exception is retryable" in new MockedEnv {
      val retryableWebClient = new RetryableJSoupWebClient(maxAttempts = 3, mockWebClient)
      mockWebClient.getResponse(testUrl).throws(serviceTemporarilyUnavailableException)
        .thenThrows(serviceTemporarilyUnavailableException)
        .thenReturns(okResponse)

      val response = retryableWebClient.get(testUrl)

      response must_== okResponse
      there were exactly(3)(mockWebClient).getResponse(testUrl)
    }

    "not retry and throw exception if exception is not retryable" in new MockedEnv {
      val retryableWebClient = new RetryableJSoupWebClient(maxAttempts = 3, mockWebClient)

      mockWebClient.getResponse(testUrl).throws(notFoundException)

      retryableWebClient.get(testUrl) must throwA[WebClientException]
      there were exactly(1)(mockWebClient).getResponse(testUrl)
    }
  }

  trait MockedEnv extends Scope {
    val testUrl = new URL("https://www.example.com")
    val mockWebClient = mock[JSoupWebClient]
    val okResponse = mock[Response]
    val serviceTemporarilyUnavailableException = new HttpStatusException("Be right back", 503, testUrl.toString)
    val notFoundException = new HttpStatusException("Not found", 404, testUrl.toString)
  }

}
