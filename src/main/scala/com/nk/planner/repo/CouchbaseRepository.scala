package com.nk.planner.repo

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.{Sink, Source}
import com.couchbase.client.java.document.JsonDocument
import com.couchbase.client.java.document.json.JsonObject
import com.nk.planner.model.Activity
import com.nk.planner.repo.CouchDriver.plannerBucket
import com.typesafe.config.{Config, ConfigFactory}
import org.reactivecouchbase.rs.scaladsl.{N1qlQuery, ReactiveCouchbase}
import play.api.libs.json.{JsValue, Json}
import org.reactivecouchbase._

import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.concurrent.duration._

object CouchbaseRepository extends CouchbaseRepository

class CouchbaseRepository {
  val system = ActorSystem("ReactiveCouchbaseSystem")

  implicit val materializer = ActorMaterializer.create(system)
  implicit val ec = system.dispatcher

  def findAll(): Future[Option[JsValue]] = Future {
    val maybeDocs = CouchDriver.plannerBucket.get("1")
    println("TEST" + maybeDocs)
    val f = Await.result(maybeDocs, 5 seconds)
    f
  }
}

object CouchDriver {

  val configString: String = {
    """
      |buckets {
      |   planner {
      |    name = "planner"
      |    hosts = ["localhost"]
      |    authentication = {
      |    username = "Administrator"
      |    password = "password"
      |    }
      |   }
      | }
    """.stripMargin
  }
  val driver = ReactiveCouchbase(ConfigFactory.parseString(configString))


  def plannerBucket = driver.bucket("planner")
}
