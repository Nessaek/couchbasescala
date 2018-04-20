package com.nk.planner.repo

import com.nk.planner.model.Activity
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.reactivecouchbase.rs.scaladsl.{N1qlQuery, ReactiveCouchbase, WriteSettings}
import spray.json.{DefaultJsonProtocol, JsonReader}

import scala.concurrent.Future
import scala.language.postfixOps
import akka.http.scaladsl.server.Directives
import org.reactivecouchbase.rs.scaladsl.json.JsonFormat
import play.api.libs.json.Json
import io.circe._
import io.circe.generic.auto._
import io.circe.parser._
import io.circe.syntax._




object CouchbaseRepository extends CouchbaseRepository

class CouchbaseRepository extends Directives with DefaultJsonProtocol  {
  val system = ActorSystem("ReactiveCouchbaseSystem")

  implicit val materializer = ActorMaterializer.create(system)
  implicit val ec = system.dispatcher

  implicit val format:JsonFormat[Activity] = Json.format[Activity]

  def findOne(docId:String): Future[Option[Activity]] = {
   CouchDriver.plannerBucket.get(docId)
  }

  def findAll(): Future[scala.Seq[String]] = {
  CouchDriver.plannerBucket.search(N1qlQuery("SELECT * FROM planner WHERE area ='area'")).map(result => result.toString).asSeq
  }

  def postOne(activity:String,area:String): Future[Activity] = {
    CouchDriver.plannerBucket.insert("9l", Activity(activity,area))
  }

  def deleteOne(docId:String): Future[Boolean] = {
    CouchDriver.plannerBucket.remove(docId)
  }

  def updateOne(docId:String, activity:String,area:String):  Future[Activity] = {
  CouchDriver.plannerBucket.replace(docId, Activity(activity,area))
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
