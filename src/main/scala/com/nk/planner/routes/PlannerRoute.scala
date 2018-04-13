package com.nk.planner.routes


import akka.http.scaladsl.model.HttpResponse
import akka.http.scaladsl.model.StatusCodes.NotFound
import akka.http.scaladsl.server.RejectionHandler
import akka.http.scaladsl.server.Directives
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import com.nk.planner.repo.PlannerRepository
import com.nk.planner.repo.CouchbaseRepository
import spray.json._
import com.nk.planner.model.Activity


trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  implicit val itemFormat = jsonFormat2(Activity)
}

class PlannerRoute extends Directives with JsonSupport {

  val activities = List("cycling", "pubbing", "walking", "eating")
  val costs = List(1, 5, 2)

  val repository: PlannerRepository = new PlannerRepository
  val couchbaseRepository: CouchbaseRepository = new CouchbaseRepository

  val plannerRoute = {
    pathPrefix("planner") {
      pathPrefix("home"){
        get{
          complete {
            (HttpResponse(200,entity = repository.introMessage(repository.callback)))

          }
        }
      } ~
     path("activities") {
        get {
          complete {
            (HttpResponse(200, entity = repository.listAllActivities(activities)))

          }
        }
      } ~
      path("activity"){
        get {
          complete {
            //Example - returning Item as Json
            PrettyPrinter(Activity("activity","area"))
          }
        }
      }
    } ~
      path("costs") {
           complete{
             HttpResponse(200, entity= repository.addAllCosts(costs, repository.applyDiscount).toString)
           }
      }

  }





//  get {
//    complete {
//      (200 -> repository.listAllActivities(activities).toString)
//
//    }
//  }


}
