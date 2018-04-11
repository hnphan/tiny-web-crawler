package com.hieuphan.tinywebscrawler.scraper

import java.net.URL

trait WebResource  {
  val url: URL
  val links: Iterable[URL]
}

