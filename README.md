# tiny-web-scrawler

* A simple web crawler written in Scala. 
* The crawler is limited to one domain - so when you start with https://example.com/, 
it would crawl all pages within example.com, but not follow external links. 
* Note that blog.example.com
is considered "external" to example.com in this implementation.
* Given a URL, it can print a simple site map, showing the links between pages.
* The crawling is done using graph breadth-first search. Links at the same "depth" in the graph can be crawled in parallel
to speed up the program.
* Web content retrieval using a wrapper around Jsoup HTTP library, which supports retrying in case of unstable connection,
and exponential back-off to avoid overloading the target website.

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

```
Give the example
```

And repeat

```
until finished
```

End with an example of getting some data out of the system or using it for a little demo

## Running the tests

Explain how to run the automated tests for this system

### Break down into end to end tests

Explain what these tests test and why

```
Give an example
```

### And coding style tests

Explain what these tests test and why

```
Give an example
```

## Deployment

Add additional notes about how to deploy this on a live system

## Built With

* [Dropwizard](http://www.dropwizard.io/1.0.2/docs/) - The web framework used
* [Maven](https://maven.apache.org/) - Dependency Management
* [ROME](https://rometools.github.io/rome/) - Used to generate RSS Feeds

## Contributing

Please read [CONTRIBUTING.md](https://gist.github.com/PurpleBooth/b24679402957c63ec426) for details on our code of conduct, and the process for submitting pull requests to us.

## Versioning

We use [SemVer](http://semver.org/) for versioning. For the versions available, see the [tags on this repository](https://github.com/your/project/tags). 

## Authors

* **Billie Thompson** - *Initial work* - [PurpleBooth](https://github.com/PurpleBooth)

See also the list of [contributors](https://github.com/your/project/contributors) who participated in this project.

## License

This project is licensed under the MIT License - see the [LICENSE.md](LICENSE.md) file for details

## Acknowledgments

* Hat tip to anyone who's code was used
* Inspiration
* etc
