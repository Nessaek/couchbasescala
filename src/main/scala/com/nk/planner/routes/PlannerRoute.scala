package com.nk.planner.routes


import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives
import org.reactivecouchbase.rs.scaladsl.json._
import org.reactivecouchbase.rs.scaladsl.circejson._
import com.nk.planner.repo.{CouchDriver, CouchbaseRepository, FutureRepository, PlannerRepository}
import spray.json._
import com.nk.planner.model.Activity
import play.api.libs.json.Json

import io.circe._, io.circe.generic.auto._, io.circe.parser._, io.circe.syntax._


import scala.util.{Failure, Success}

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer





class PlannerRoute extends Directives {

  val future: FutureRepository = new FutureRepository
  val repository: PlannerRepository = new PlannerRepository
  val couchbaseRepository: CouchbaseRepository = new CouchbaseRepository
  implicit val system       = ActorSystem("ReactiveCouchbaseSystem")
  implicit val materializer = ActorMaterializer.create(system)
  implicit val ec           = system.dispatcher

  val plannerRoute = {
    pathPrefix("planner") {
      pathPrefix("home") {
        get {
          complete {
            HttpResponse(200, entity = repository.introMessage(repository.callback))

          }
        }
      } ~
        pathPrefix("get") {

          {
            onComplete(couchbaseRepository.findAll) {
              case Success(result) => complete(s"$result")
              case Failure(e) => complete(StatusCodes.InternalServerError)
            }
          }
            path(Segment) { id =>
              onComplete(couchbaseRepository.findOne(id)) {
                  case Success(result) =>
                    complete( HttpResponse(entity = HttpEntity(ContentTypes.`application/json`, result)))
                  case Failure(e) => complete(StatusCodes.InternalServerError)
                }

            }
          }
        } ~
        pathPrefix("post") {
          post {
           parameters('activity.as[String],'area.as[String]){(activity,area) =>
            onComplete(couchbaseRepository.postOne(activity,area)) {
              case Success(result) => complete(HttpResponse(200,entity="item successfully posted"))
              case Failure(e) => complete(StatusCodes.InternalServerError)
            }
           }
          }
        } ~ path(Segment) {
        id => {
          delete {
            onComplete(couchbaseRepository.deleteOne(id)) {
              case Success(result) => complete(HttpResponse(200,entity="entity successfully deleted"))
              case Failure(e) => complete(StatusCodes.InternalServerError)
            }
          }
              }
            } ~ path(Segment) {
        id => {
          put {
            parameters('activity.as[String],'area.as[String]) {
              (activity, area) =>
              onComplete(couchbaseRepository.updateOne(id,activity,area)) {
                case Success(result) => complete(HttpResponse(200,entity = "successfully updated"))
                case Failure(e) => complete(StatusCodes.InternalServerError)
              }
            }
          }
        }
      }
          }

      }


