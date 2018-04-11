package com.hieuphan.tinywebscrawler.crawler

import java.net.URL

import com.hieuphan.tinywebscrawler.scraper.WebResource

trait Crawler {
  def crawl(startUrl: URL, depth: Int = Int.MaxValue): Iterable[WebResource]
}






