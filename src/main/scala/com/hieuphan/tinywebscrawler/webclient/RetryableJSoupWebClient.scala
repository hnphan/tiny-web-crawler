package com.hieuphan.tinywebscrawler.webclient

import java.net.URL

import com.typesafe.scalalogging.LazyLogging
import org.jsoup.Connection.Response
import org.jsoup.HttpStatusException

import scala.concurrent.{ExecutionContext}
import scala.math.pow

class RetryableJSoupWebClient(maxAttempts: Int = 3, jSoupWebClient: JSoupWebClient) extends LazyLogging {

  require(maxAttempts > 1)

  // this list is not exhaustive, needs reviewing
  val RETRYABLE_HTTP_CODES = Seq(503)

  def get(url: URL, attemptsSoFar: Int = 0)(implicit ec: ExecutionContext): Response = {
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
