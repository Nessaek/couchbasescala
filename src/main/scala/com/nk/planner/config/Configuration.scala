package com.nk.planner.config

import com.typesafe.config.{Config, ConfigFactory, ConfigResolveOptions}

import scala.collection.JavaConversions._
import scala.collection.JavaConverters._
import scala.concurrent.duration._
import scala.language.postfixOps
import scala.util.Try

object Configuration extends Configuration {
  val conf = config
}

trait Configuration {

  def config = ConfigFactory.load()
    .resolve()
    .withFallback(ConfigFactory.load("genres"))

  lazy val funcTestEnabled = Try(sys.env("FUNC_TEST_ENDPOINTS_ENABLED").toBoolean).toOption.getOrElse(false)
  lazy val smartlistLimit = config.getInt("smartlist.limit")

  lazy val umvHost = config.getString("umv.host")
  lazy val umvClient = config.getString("umv.client")
  lazy val umvSecret = config.getString("umv.secret")
  lazy val umvRetries = config.getInt("umv.retries")
  lazy val umvTimeout = config.getInt("umv.timeout")

  lazy val oogwayUsername = config.getString("oogway.http.username")
  lazy val oogwayPassword = config.getString("oogway.http.password")
  lazy val oogwayResourceBase = config.getString("oogway.resource.base")
  lazy val oogwayGetProfileByOauth = config.getString("oogway.get.profile.oauth")
  lazy val oogwayGetProfileBySso = config.getString("oogway.get.profile.sso")

  lazy val isKamonEnabled = config.getBoolean("monitoring.kamonEnabled")
  lazy val kamonStatsdHostname = config.getString("kamon.statsd.hostname")

  lazy val httpPort = config.getInt("http.port")
  lazy val allowedHttpHeaders = Try(config.getStringList("http.allowedHeaders").asScala.toList).toOption.getOrElse(Nil)
  lazy val authenticationHeaders = config.getStringList("authentication.headers")

  lazy val couchbaseDataNodes = Try(config.getString("cb_data").split(",").map(_.trim).toSeq).toOption.getOrElse(Nil)
  lazy val couchbaseBucketPassword = config.getString("cb_bucket_password")
  lazy val couchbaseN1QlNodes = Try(config.getString("cb_n1ql")).toOption.getOrElse("")

  lazy val engineNode = config.getString("engine.node")

  lazy val mapReduceTitleDocTTL = config.getInt("mapReduce.titleDocTTL") days
  lazy val territories = config.getString("territories").split(",").map(_.trim).toSeq

  lazy val umvSupportedIdsMap = config.getString("umv.territoryPropositionAuthMethods").split(",").map(_.trim).map(_.split("::")).map(a => a(0) -> a(1)).toMap

  private implicit class GenreMapConversions(conf: Config) {
    def genresAsScalaMap(key: String) = conf.getObject("genres")(key)
      .unwrapped()
      .asInstanceOf[java.util.Map[String, AnyRef]]
      .mapValues(_.asInstanceOf[java.util.Map[String, AnyRef]].asScala)

    def metaGenreFieldsAsScalaMap(key: String) = conf.getObject("genres")(key)
      .unwrapped()
      .asInstanceOf[java.util.Map[String, String]]
      .asScala
  }

  lazy val airingGenres = config.genresAsScalaMap("airing")

  lazy val metaGenres = config.genresAsScalaMap("meta")

  lazy val dqGenreFieldFor = config.metaGenreFieldsAsScalaMap("metaGenreField")

  lazy val cddrJanitorUsername = config.getString("cddr.janitor.username")
  lazy val cddrJanitorPassword = config.getString("cddr.janitor.password")
  lazy val cddrJanitorPayloadType = config.getString("cddr.janitor.payloadType")
}

