package com.hieuphan.tinywebscrawler.scraper

import java.net.{SocketTimeoutException, URL}

import com.typesafe.scalalogging.LazyLogging
import org.jsoup.Connection.Response
import org.jsoup.{Connection, HttpStatusException, Jsoup}

import scala.concurrent.Future
import scala.math.pow

trait Scraper {
  def scrape(url: URL): WebResource
}

