package com.hieuphan.tinywebscrawler.scraper

import java.net.URL

case class SimpleWebResource(url: URL, links: Set[URL]) extends WebResource

