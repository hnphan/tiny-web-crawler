name := "tiny-web-crawler"

version := "0.1"

scalaVersion := "2.12.5"

libraryDependencies += "org.jsoup" % "jsoup" % "1.8.3"
libraryDependencies += "org.scala-graph" %% "graph-core" % "1.12.3"
libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0"

resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"

