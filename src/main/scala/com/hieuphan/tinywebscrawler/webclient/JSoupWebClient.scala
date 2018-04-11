package com.hieuphan.tinywebscrawler.webclient

import java.io.IOException
import java.net.URL

import org.jsoup.Connection.Response
import org.jsoup.Jsoup

class JSoupWebClient {

  @throws[IOException]
  def getResponse(url: URL): Response  = {
    Jsoup.connect(url.toString).ignoreContentType(true)
      .userAgent("Totally not a bot")
      .execute()
  }
  
}
