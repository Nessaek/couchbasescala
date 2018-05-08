

name := "couchbasescala"

version := "0.1"

scalaVersion := "2.12.2"

libraryDependencies ++= {
  val AkkaVersion = "2.4.18"
  val AkkaHttpVersion = "10.0.6"
  val Json4sVersion = "3.5.2"
  Seq(
    "com.typesafe.akka" %% "akka-slf4j"      % AkkaVersion,
    "com.typesafe.akka" %% "akka-http" % AkkaHttpVersion,
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "org.json4s"        %% "json4s-native"   % Json4sVersion,
    "org.json4s"        %% "json4s-ext"      % Json4sVersion,
    "de.heikoseeberger" %% "akka-http-json4s" % "1.16.0",
    "de.heikoseeberger" %% "akka-http-jackson" % "1.18.0",
    "ch.qos.logback" % "logback-classic" % "1.2.3",
    "com.typesafe.scala-logging" %% "scala-logging" % "3.9.0",
    "com.typesafe.akka" %% "akka-http-spray-json" % "10.1.1",
    "org.reactivecouchbase" %% "reactivecouchbase-rs-core" % "1.2.1",

    //TESTING
    "io.cucumber" %% "cucumber-scala" % "2.0.1" % "test",
    "info.cukes" % "cucumber-junit" % "1.2.4",
    "junit" % "junit" % "4.12",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test"

  )
}