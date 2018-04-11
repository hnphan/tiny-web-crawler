package com.hieuphan.tinywebscrawler.webclient

import java.net.URL

import com.typesafe.scalalogging.LazyLogging
import org.jsoup.Connection.Response
import org.jsoup.HttpStatusException
import scala.math.pow

class RetryableJsoupWebClient(jSoupWebClient: JsoupWebClient, maxAttempts: Int = 3) extends LazyLogging {

  require(maxAttempts > 1)

  // this list might not be exhaustive, needs reviewing
  val RETRYABLE_HTTP_CODES = Seq(503)

  @throws[WebClientException]
  def get(url: URL, attemptsSoFar: Int = 0): Response = {
    try {
      jSoupWebClient.getResponse(url)
    }
    catch {
      case e: HttpStatusException =>
        if (isExceptionRetryable(e) && attemptsSoFar < maxAttempts) {
          Thread.sleep(1000 * pow(2, attemptsSoFar).toLong) // exponential back off
          get(url, attemptsSoFar + 1)
        }
        else throw WebClientException(e.getMessage, e.getCause)
      case e: Throwable => throw WebClientException(e.getMessage, e.getCause)
    }
  }

  private def isExceptionRetryable(e: HttpStatusException): Boolean = {
    return RETRYABLE_HTTP_CODES.contains(e.getStatusCode)
  }

}

object RetryableJsoupWebClient {
  def apply(maxAttempts: Int = 3) = {
    new RetryableJsoupWebClient(new JsoupWebClient())
  }
}
