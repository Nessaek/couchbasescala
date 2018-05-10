package com.nk.crud.routes


import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directives, ExceptionHandler, Route}
import com.nk.crud.repo.{CouchDriver, CouchbaseRepository}
import spray.json._

import scala.util.{Failure, Success}
import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.server
import akka.stream.ActorMaterializer
import com.couchbase.client.java.error
import com.couchbase.client.java.error.DocumentDoesNotExistException
import com.nk.crud.model.{Activity, Session}
import com.nk.crud.services.AuthenticationService
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.User
import com.typesafe.scalalogging.LazyLogging
import akka.actor.ActorSystem
import com.softwaremill.session.CsrfDirectives._
import com.softwaremill.session.CsrfOptions._
import com.softwaremill.session.SessionDirectives._
import com.softwaremill.session.SessionOptions._
import com.softwaremill.session._
import akka.stream.ActorMaterializer
import com.softwaremill.session.{BasicSessionEncoder, SessionConfig, SessionManager, SessionSerializer}
import org.reactivecouchbase.rs.scaladsl.json.JsonFormat
import play.api.libs.json.Json


trait FormatMe extends SprayJsonSupport with DefaultJsonProtocol{
  implicit val activityFormat = jsonFormat3(Activity)
  implicit val format: JsonFormat[Session] = Json.format[Session]

}



class CouchRoute extends Directives with LazyLogging with FormatMe {
  val sessionConfig =
    SessionConfig.default("very_very_very_very_very_very_secret_session_string_of_64_or_more_letters")
  implicit val sessionManager = new SessionManager[Session](sessionConfig)

  val couchbaseRepository: CouchbaseRepository = new CouchbaseRepository
  val authService: AuthenticationService = new AuthenticationService

  implicit val system = ActorSystem("ReactiveCouchbaseSystem")
  implicit val materializer = ActorMaterializer.create(system)
  implicit val ec = system.dispatcher
  implicit val refreshTokenStorage = new InMemoryRefreshTokenStorage[Session] {
    def log(msg: String) = logger.info(msg)
  }

  val crudRoute = handleExceptions(myExceptionHandler) {

    randomTokenCsrfProtection(checkHeader) {
      pathPrefix("suggestion") {
        pathPrefix("security") {

          post {
            entity(as[String]) { body =>
              logger.info(s"Logging in $body")
              setSession(refreshable, usingCookies, Session(body)) {
                setNewCsrfToken(checkHeader) { ctx => ctx.complete("ok") }
              }

            }
          } ~
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
            entity(as[Activity]) { activity =>
              onComplete(couchbaseRepository.postOne(activity)) {
                case Success(result) => complete(result)
                case Failure(e) => complete(StatusCodes.InternalServerError)
              }
            }
          } ~ put {
            path(Segment) { id =>
              entity(as[Activity]) { activity =>
                complete(201, couchbaseRepository.updateOne(id, activity))

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
    def myExceptionHandler: ExceptionHandler =
      ExceptionHandler {
        case _: DocumentDoesNotExistException =>
          println(s"Request to could not be handled normally")
          complete(HttpResponse(404, entity = "document not found"))

      }


}