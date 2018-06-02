package com.nk.crud.repo

import com.nk.crud.model.{Task,TaskUpdate}
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import com.typesafe.config.ConfigFactory
import org.reactivecouchbase.rs.scaladsl.{N1qlQuery, ReactiveCouchbase}
import spray.json.DefaultJsonProtocol

import scala.concurrent.Future

//import scala.concurrent.Future
import scala.language.postfixOps
import akka.http.scaladsl.server.Directives
import org.reactivecouchbase.rs.scaladsl.json.JsonFormat
import play.api.libs.json.Json



object CouchbaseRepository extends CouchbaseRepository

class CouchbaseRepository extends Directives with DefaultJsonProtocol {

  val system = ActorSystem("ReactiveCouchbaseSystem")

  implicit val materializer = ActorMaterializer.create(system)
  implicit val ec = system.dispatcher
  implicit val format: JsonFormat[Task] = Json.format[Task]



  def findOne(docId: String): Future[Option[Task]] = {
    CouchDriver.plannerBucket.get(docId)
  }


  def findAll(): Future[Seq[Task]] = {
    CouchDriver.plannerBucket.search(N1qlQuery("select planner.* from planner")).map(result => result).asSeq
  }


  def postOne(task: Task): Future[Option[Task]] = {

    findOne(task.id).flatMap{
      case None => CouchDriver.plannerBucket.insert(task.id,task).map(result =>
        Some(result))
      case Some(_) => Future(None)
    }


  }


  def deleteOne(docId: String): Future[Boolean] = {
     CouchDriver.plannerBucket.remove(docId)

  }

  def deleteAll(): Future[scala.Boolean] = {
   CouchDriver.plannerBucket.withManager(_.flush()).map(result => result)
  }


  def updateOne(docId:String,update: Option[TaskUpdate]): Future[Option[Task]] = {

    def updateEntity(taskEntity: Task): Task = {

      update match {
        case None =>  val task = taskEntity.task
          val area = taskEntity.area
          val enabled = if(taskEntity.enabled == 1) 0 else 1
          Task(docId, task, area, enabled)

        case Some(someUpdate) =>  val task = someUpdate.task.getOrElse(taskEntity.task)
          val area = someUpdate.area.getOrElse(taskEntity.area)
          val enabled = taskEntity.enabled
          Task(docId, task, area, enabled)
      }

    }

    findOne(docId).flatMap {
      case None => Future { None }
      case Some(foundTask) =>
        val updatedTask = updateEntity(foundTask)
        deleteOne(docId).flatMap { _ =>
          postOne(updatedTask).map(_ => Some(updatedTask))
        }
    }

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
