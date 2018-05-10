enablePlugins(JavaServerAppPackaging)
enablePlugins(DockerPlugin)
enablePlugins(AshScriptPlugin)

dockerBaseImage       := "openjdk:jre-alpine"


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
     "com.softwaremill.akka-http-session" %% "core" % "0.5.5",
     "com.softwaremill.akka-http-session" %% "jwt"  % "0.5.5",
    //TESTING
    "io.cucumber" %% "cucumber-scala" % "2.0.1" % "test",
    "info.cukes" % "cucumber-junit" % "1.2.4",
    "junit" % "junit" % "4.12",
    "org.scalatest" %% "scalatest" % "3.0.5" % "test"

  )
}

test in assembly := {}
// Simple and constant jar name
assemblyJarName in assembly := s"app-assembly.jar"
// Merge strategy for assembling conflicts
assemblyMergeStrategy in assembly := {
  case PathList("reference.conf") => MergeStrategy.concat
  case PathList("META-INF", "MANIFEST.MF") => MergeStrategy.discard
  case _ => MergeStrategy.first
}