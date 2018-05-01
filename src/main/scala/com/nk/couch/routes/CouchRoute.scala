package com.nk.couch.routes


import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.{Directives, Route}
import com.nk.couch.repo.{CouchDriver, CouchbaseRepository}

import spray.json._

import scala.util.{Failure, Success}
import akka.actor.ActorSystem
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.stream.ActorMaterializer
import com.nk.couch.model.Activity
import com.nk.couch.services.AuthenticationService
import com.sun.tools.internal.xjc.reader.xmlschema.bindinfo.BIConversion.User
import com.typesafe.scalalogging.LazyLogging

import scala.None
import scala.concurrent.Future


trait FormatMe extends SprayJsonSupport with DefaultJsonProtocol{
  implicit val activityFormat = jsonFormat2(Activity)


}

class PlannerRoute extends Directives with LazyLogging with FormatMe {

  val couchbaseRepository: CouchbaseRepository = new CouchbaseRepository
  val authService: AuthenticationService = new AuthenticationService

  implicit val system = ActorSystem("ReactiveCouchbaseSystem")
  implicit val materializer = ActorMaterializer.create(system)
  implicit val ec = system.dispatcher


  val plannerRoute = {
    pathPrefix("suggestion") {
      complete(200 -> "test")
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

                    case Success(result) =>
                      complete(result)
                    case Failure(e) => complete(e)
                  }
              }
            } ~ post {
              entity(as[Activity]){activity =>
                onComplete(couchbaseRepository.postOne(activity)) {
                  case Success(result) => complete(result)
                  case Failure(e) => complete(StatusCodes.InternalServerError)
                }
              }
            } ~ put {
              path(Segment) {
                id => {
                  parameters('activity.as[String], 'area.as[String]) {
                    (activity, area) =>
                      onComplete(couchbaseRepository.updateOne(id, activity, area)) {
                        case Success(result) => complete(HttpResponse(200, entity = "successfully updated"))
                        case Failure(e) => complete(StatusCodes.InternalServerError)
                      }
                  }
                }
              }
            } ~
              delete {
                rejectEmptyResponse {
                  path(Segment) {
                    id => {
                      onSuccess(couchbaseRepository.deleteOne(id)){
                        case true => complete(HttpResponse(200, entity = "entity successfully deleted"))
                        case false => complete(HttpResponse(404,entity="entity not found"))
                      }
                      }
                    }
                  }
                }
              }

          }
        }
      }
    }
}
