package com.nk.crud.repo

import com.nk.crud.model.Activity
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.couchbase.client.java.error.DocumentDoesNotExistException
import com.typesafe.config.ConfigFactory
import org.reactivecouchbase.rs.scaladsl.{N1qlQuery, ReactiveCouchbase}
import spray.json.{DefaultJsonProtocol, JsonReader}

import scala.concurrent.{Await, Future}

//import scala.concurrent.Future
import scala.language.postfixOps
import akka.http.scaladsl.server.Directives
import org.reactivecouchbase.rs.scaladsl.json.{JsonFormat, JsonReads}
import play.api.libs.json.{JsValue, Json}
import java.util.UUID.randomUUID
import scala.concurrent.duration._



object CouchbaseRepository extends CouchbaseRepository

class CouchbaseRepository extends Directives with DefaultJsonProtocol {

  val system = ActorSystem("ReactiveCouchbaseSystem")

  implicit val materializer = ActorMaterializer.create(system)
  implicit val ec = system.dispatcher
  implicit val format: JsonFormat[Activity] = Json.format[Activity]



  def findOne(docId: String): Future[Option[Activity]] = {
    CouchDriver.plannerBucket.get(docId)
  }


  def findAll(): Future[Seq[Activity]] = {
    CouchDriver.plannerBucket.search(N1qlQuery("select planner.* from planner")).map(result => result).asSeq
  }


  def postOne(activity: Activity): Future[Activity] = {
    CouchDriver.plannerBucket.insert(activity.id,activity).map(result =>
      result)

  }


  def deleteOne(docId: String): Future[Boolean] = {
     CouchDriver.plannerBucket.remove(docId)

  }

  def deleteAll():  Future[scala.Boolean] = {
   CouchDriver.plannerBucket.withManager(_.flush()).map(result => result)
  }


  def updateOne(docId:String,activity: Activity): Future[Activity] = {
      CouchDriver.plannerBucket.replace(docId, activity)

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
