name := "tiny-web-crawler"

version := "0.1"

scalaVersion := "2.12.5"

libraryDependencies += "org.jsoup" % "jsoup" % "1.8.3"
libraryDependencies += "com.github.scopt" %% "scopt" % "3.7.0"
libraryDependencies += "ch.qos.logback" % "logback-classic" % "1.2.3"
libraryDependencies += "com.typesafe.scala-logging" %% "scala-logging" % "3.8.0"
libraryDependencies += "commons-validator" % "commons-validator" % "1.5+"
libraryDependencies += "com.google.guava" % "guava" % "19.0"
libraryDependencies += "org.specs2" %% "specs2-core" % "4.0.3" % Test
libraryDependencies += "org.specs2" %% "specs2-mock" % "4.0.3" % Test
//libraryDependencies += "junit" % "junit" % "4.12" % Test

scalacOptions in Test ++= Seq("-Yrangepos")
resolvers += "Java.net Maven2 Repository" at "http://download.java.net/maven/2/"
