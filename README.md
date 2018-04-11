# tiny-web-scrawler

* A simple web crawler written in Scala. 
* The crawler is limited to one domain - so when you start with https://example.com/, 
it would crawl all pages within example.com, but not follow external links. 
* Note that blog.example.com and example.com
are considered two different domains in this implementation.
* Given a URL, it can print a simple site map, showing the links between pages.

### Technical overview
* The code is structured into a few building blocks:
  * package `webclient` contains classes which help retrieve web content (such as html pages)
  over HTTP. The code supports retrying & exponential backout for robustness.
  * package `scraper` contains classes which help scrape links from html content
  * package `crawler` contain classes which orchestrate the crawling work in parallel
* The crawling is done using graph breadth-first search. 
* Links at the same "depth" in the graph can be crawled in parallel
to speed up the program. To simplify the concurrency model, we only crawl links at one depth at a time.
* The concurrency is simply done by using an executor with an underlying fixed thread pool. Scala futures are automatically
executed in parallel using this executor.
* Web content retrieval is done using a wrapper around Jsoup HTTP library, which supports retrying in case of unstable connection,
and exponential back-off to avoid overloading the target website.

### Known limitations
* There are some subtle cases such as `http://www.w3.org` and `http://www.w3.org/` are considered two different URL's by this crawler
* This crawler is rather impolite; it does not respect `robots.txt`

### Getting started
Get a copy of the code from:
```$xslt
git clone git@github.com:hnphan/tiny-web-crawler.git
```
### Prerequisites

* Make sure you have JDK installed, if not: https://docs.oracle.com/javase/8/docs/technotes/guides/install/install_overview.html

* Install sbt: https://www.scala-sbt.org/1.0/docs/Setup.html, or simply:

```
brew install sbt
```

### Running instruction

Once you have sbt install, you can play around with the program by following the examples below:

First navigate into the top directory:
```
cd tiny-web-crawler
```

Print usage:
```
sbt "run --help"
Welcome to tiny web crawler 0.1
Usage: tiny-thread-crawler [options]

  -u, --url <startUrl>     
  -d, --depth <maxDepth> (default to Int.MaxValue)
                           
  -c, --concurrency <maxConcurrency> (default to 8)
                           
  --help                   prints this usage text


```

Crawl from https://www.w3.org with default options
```
sbt "run -u https://www.w3.org"
```
Crawl from https://www.w3.org using 16 threads
```
sbt "run -u https://www.w3.org -c 16"
```
Crawl from https://www.w3.org using 16 threads, stopping after 2 levels
```
sbt "run -u https://www.w3.org -c 16 -d 3"
```
Sample stdout output:
```
00:19:20.571 [run-main-0] INFO com.hieuphan.tinywebscrawler.crawler.ConcurrentCrawler - Will start crawling from http://www.w3.org using maxConcurrency 8
00:19:20.735 [run-main-0] DEBUG com.hieuphan.tinywebscrawler.crawler.ConcurrentCrawler - Crawling at depth 0
00:19:21.316 [run-main-0] DEBUG com.hieuphan.tinywebscrawler.crawler.ConcurrentCrawler - Crawling at depth 1
00:19:24.344 [pool-8-thread-7] WARN com.hieuphan.tinywebscrawler.scraper.RetryableInternalLinksScraper - Oops, something bad happened when trying to visit http://www.w3.org/Member/.
00:19:27.331 [pool-8-thread-1] WARN com.hieuphan.tinywebscrawler.scraper.RetryableInternalLinksScraper - Oops, something bad happened when trying to visit http://www.w3.org/Consortium/activities.
00:19:27.862 [run-main-0] INFO com.hieuphan.tinywebscrawler.Main$ - Done. Elapsed time: 7.37301609s
00:19:27.911 [run-main-0] INFO com.hieuphan.tinywebscrawler.Main$ - 
http://www.w3.org
		https://www.w3.org/TR/2018/WD-wot-thing-description-20180405/
		http://www.w3.org/community/
		https://www.w3.org/International/core/Overview
		http://www.w3.org/Status.html
		http://www.w3.org/2009/cheatsheet/
		http://www.w3.org/Consortium/Legal/ipr-notice
		http://www.w3.org/blog/news/feed
		http://www.w3.org/participate/
		http://www.w3.org/community/groups/
		https://www.w3.org/blog/2018/04/w3cs-wai-act-project-identified-as-key-innovator/
		http://www.w3.org/Consortium/Member/Testimonial/
		https://www.w3.org/blog/talks/venue/funka-days/
		https://www.w3.org/blog/news/archives/6948
		https://www.w3.org/blog/2018/03/publishing-w3c-goes-to-ebookcraft/
		https://www.w3.org/WAI/videos/standards-and-benefits.html
		https://www.w3.org/blog/news/archives/6945
		https://www.w3.org/2018/vocabws/
		https://www.w3.org/2018/Process-20180201/
		http://www.w3.org/2013/data/
		https://www.w3.org/webauthn/

[...]

http://www.w3.org/2009/cheatsheet/
		http://www.w3.org/International/getting-started/language
		http://www.w3.org/2009/cheatsheet/
		http://www.w3.org/International/getting-started/characters
		http://www.w3.org/WAI/intro/wcag.php
		http://www.w3.org/International/questions/qa-escapes
		http://www.w3.org/International/techniques/server-setup
		http://www.w3.org/2007/Talks/0706-atmedia/slides/Slide0350.html
		http://www.w3.org/International/techniques/authoring-svg
		http://www.w3.org/WAI/WCAG20/quickref/
		http://www.w3.org/International/techniques/authoring-html
		http://www.w3.org/International/articles/inline-bidi-markup/
		http://www.w3.org/International/techniques/authoring-xml
		http://www.w3.org/International/questions/qa-what-is-encoding
		http://www.w3.org/International/
		http://www.w3.org/International/tutorials/bidi-xhtml/
		http://www.w3.org/International/techniques/developing-schemas
		http://www.w3.org/International/techniques/developing-specs
		http://www.w3.org/2009/cheatsheet/@@@
		http://www.w3.org/2007/Talks/0706-atmedia/slides/Slide0440.html 

[...]
```

## Running the tests
Unit tests are written with specs2 & mockito, which makes the tests a human-readable specification
for this project, for example:
```scala
class RetryableJsoupWebClientTest extends Specification with Mockito {

  "RetryableJSoupWebClient" should {
    "issue request and get response successfully" in new MockedEnv {
      val retryableWebClient = new RetryableJsoupWebClient(mockWebClient, maxAttempts = 3)
      mockWebClient.getResponse(testUrl) returns okResponse

      val response = retryableWebClient.get(testUrl)
      response must_== okResponse
    }

    "retry according to specified maxAttempts if exception is retryable" in new MockedEnv {
      val retryableWebClient = new RetryableJsoupWebClient(mockWebClient, maxAttempts = 3)
      mockWebClient.getResponse(testUrl).throws(serviceTemporarilyUnavailableException)
        .thenThrows(serviceTemporarilyUnavailableException)
        .thenReturns(okResponse)

      val response = retryableWebClient.get(testUrl)

      response must_== okResponse
      there were exactly(3)(mockWebClient).getResponse(testUrl)
    }
```
To run the tests, simply run
```
sbt test
```

