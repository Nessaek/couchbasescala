package com.nk.planner.routes


import akka.http.scaladsl.model.{ContentTypes, HttpEntity, HttpResponse, StatusCodes}
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import akka.http.scaladsl.marshalling.ToResponseMarshallable
import com.nk.planner.repo.{CouchDriver, CouchbaseRepository, FutureRepository, PlannerRepository}
import spray.json._
import com.nk.planner.model.Activity

import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemFormat = jsonFormat2(Activity)
}

class PlannerRoute extends Directives with JsonSupport {

  val future: FutureRepository = new FutureRepository
  val repository: PlannerRepository = new PlannerRepository
  val couchbaseRepository: CouchbaseRepository = new CouchbaseRepository

  val input =
    """{"uid":1,"txt":"#Akka rocks!"}""" + "\n" +
      """{"uid":2,"txt":"Streaming is so hot right now!"}""" + "\n" +
      """{"uid":3,"txt":"You cannot enter the same river twice."}"""

  val number: Option[Int] = None
  val activities = List("cycling", "pubbing", "walking", "eating")
  val costs = List(1, 5, 2)
  val tuplesList = List(("Running", 5, 2.50), ("Swimming", 10, 3.50))
  val map = Map("sport" -> "cycling", "music" -> "keyboard", "tv" -> "crazy ex-girlfriend")

  val futureOperationsList = List(future.findKeyInList(activities, "test"),
    future.findKeyInList(activities, "test1"),
    future.findKeyInList(activities, "test2")
  )
  val futureOperationsIntList = List(future.findIndexInList(activities, "test"),
    future.findIndexInList(activities, "test1"),
    future.findIndexInList(activities, "test2")
  )


  val plannerRoute = {
    pathPrefix("planner") {
      pathPrefix("home") {
        get {
          complete {
            HttpResponse(200, entity = repository.introMessage(repository.callback))

            //example - returning text as json
            //            HttpResponse(entity = HttpEntity(ContentTypes.`application/json`, input))

          }
        }
      } ~
        path("activities") {
          get {
            complete {
              HttpResponse(200, entity = repository.listAllActivities(activities))

            }
          }
        } ~
        path("all") {
          get {
            onComplete(future.addStuff(1, 2).flatMap(result => future.minusStuff(result, 2))) {
              case Success(result) => complete(s"you have $result")
              case Failure(e) => complete(StatusCodes.InternalServerError)

              //     get {
              //         complete(
              //         Activity("activity", "area")
              // couchbaseRepository.findAll
              //                            map(v => {
              //                            HttpResponse(200, entity = HttpEntity(ContentTypes.`application/json`, v))
              //                          })
              //           )

            }
          }
          //}
        } ~
        path("activity") {
          get {
            complete {
              //Example - returning Item as Json
              Activity("activity", "area")

            }
          }
        } ~
        path("costs") {
          complete(
            HttpResponse(200, entity = repository.addAllCosts(costs, repository.applyDiscount).toString)
          )
        } ~
        path("chained-futures") {
          onComplete(future.chainedFuturesWithFor(future.addStuff(2, 6), future.minusStuff(3, 2))) {
            case Success(result) => complete(s"$result")
            case Failure(e) => complete(StatusCodes.InternalServerError)
          }
        } ~
        path("futures-with-options") {
          onComplete(future.findKeyInList(activities, "crazy")) {
            case Success(result) => complete(s"$result")
            case Failure(e) => complete(StatusCodes.InternalServerError)
          }
        } ~
        path("chained-future-options") {
          onComplete(future.chainedFutureOptions(future.addStuff(number.getOrElse(2), 4), future.minusStuff(3, 3))) {
            case Success(result) => complete(s"$result")
            case Failure(e) => complete(StatusCodes.InternalServerError)
          }
        } ~
        path("access-future-with-map") {
          complete(future.findKeyInList(activities, "crazy").map(result => s"$result"))
        } ~
        path("future-sequence") {
          onComplete(future.futureOperations) {
            case Success(result) => complete(s"$result")
            case Failure(e) => complete(StatusCodes.InternalServerError)
          }
        } ~
        path("future-traverse") {
          onComplete(future.futureTraverse(futureOperationsList)) {
            case Success(result) => complete(s"$result")
            case Failure(e) => complete(StatusCodes.InternalServerError)
          }
        } ~
        path("future-fold-left") {
          onComplete(future.futureFoldLeft(futureOperationsIntList)) {
            case Success(result) => complete(s"$result")
            case Failure(e) => complete(StatusCodes.InternalServerError)
          }
        } ~
        path("future-completed-of") {
          onComplete(future.futureFirstCompletedOf(futureOperationsIntList)) {
            case Success(result) => complete(s"$result")
            case Failure(e) => complete(StatusCodes.InternalServerError)
          }
        } ~
        path("future-zip") {
          onComplete(future.futureZip(future.findIndexInList(activities, "dfgsdfg"), future.findKeyInList(activities, "cycling"))) {
            case Success(result) => complete(s"$result")
            case Failure(e) => complete(StatusCodes.InternalServerError)
          }
        } ~
        path("couchbase") {
          onComplete(couchbaseRepository.findAll()) {
            case Success(result) => complete(s"$result")
            case Failure(e) => complete(StatusCodes.InternalServerError)
          }
        }
    }
  }

}