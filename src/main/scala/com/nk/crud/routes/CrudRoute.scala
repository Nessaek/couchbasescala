package com.nk.crud.routes


import akka.http.scaladsl.model.{HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directives, ExceptionHandler}
import com.nk.crud.repo.CouchbaseRepository
import spray.json._

import scala.util.{Failure, Success}
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.couchbase.client.java.error.DocumentDoesNotExistException
import com.nk.crud.model.{Task,TaskUpdate}
import com.nk.crud.services.AuthenticationService
import com.typesafe.scalalogging.LazyLogging
import akka.actor.ActorSystem
import akka.stream.ActorMaterializer

trait FormatMe extends SprayJsonSupport with DefaultJsonProtocol{
  implicit val taskFormat = jsonFormat3(Task)
  implicit val taskUpdateFormat = jsonFormat2(TaskUpdate)

}



class CouchRoute extends Directives with LazyLogging with FormatMe {

  val couchbaseRepository: CouchbaseRepository = new CouchbaseRepository
  val authService: AuthenticationService = new AuthenticationService

  implicit val system = ActorSystem("ReactiveCouchbaseSystem")
  implicit val materializer = ActorMaterializer.create(system)
  implicit val ec = system.dispatcher

  val crudRoute = handleExceptions(myExceptionHandler) {
      pathPrefix("suggestion") {
        pathPrefix("security") {
          authenticateBasicAsync(realm = "secure site", authService.myUserPassAuthenticator) {
            loggedIn => {
                get {
                  rejectEmptyResponse {
                    path(Segment) { id => {
                      onSuccess(couchbaseRepository.findOne(id)) { result =>
                        complete(result)

                      }
                    }
                    } ~ onComplete(couchbaseRepository.findAll) {

                      case Success(result) => complete(result)
                      case Failure(e) => complete(e)
                    }
                  }
                } ~ post {
                entity(as[Task]) { task =>
                  onComplete(couchbaseRepository.postOne(task)) {
                    case Success(result) => complete(result)
                    case Failure(e) => complete(StatusCodes.InternalServerError)
                  }
                }
              } ~ put {
                path(Segment) { id =>
                  entity(as[TaskUpdate]) { task =>
                    complete(201, couchbaseRepository.updateOne(id, task))

                  }

                }

              } ~
                delete {
                  path(Segment) {
                    id => {
                      onSuccess(couchbaseRepository.deleteOne(id)) {
                        case true => complete(204, HttpEntity.Empty)
                        case false => complete(HttpResponse(404, entity = "entity not found"))
                      }
                    }
                  } ~ {
                    onSuccess(couchbaseRepository.deleteAll()) {
                      case true => complete(HttpResponse(200, entity = "bucket succesfully flushed"))
                      case false => complete(StatusCodes.InternalServerError)
                    }
                  }

                }
            }
          }
        }
      }
      }
    def myExceptionHandler: ExceptionHandler =
      ExceptionHandler {
        case _: DocumentDoesNotExistException =>
          println(s"Request to could not be handled normally")
          complete(HttpResponse(404, entity = "document not found"))

      }


}