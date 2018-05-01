package com.nk.couch.steps

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials}
import akka.stream.ActorMaterializer
import com.nk.couch.model.Activity
import com.nk.couch.repo.CouchbaseRepository
import cucumber.api.scala.{EN, ScalaDsl}

import scala.concurrent.{Await, Future, duration}
import scala.concurrent.duration._
import org.scalatest.Matchers._

import scala.language.postfixOps



class StepDefinitions extends ScalaDsl with EN {

  implicit val system = ActorSystem("ReactiveCouchbaseSystem")
  implicit val materializer = ActorMaterializer.create(system)
  implicit val ec = system.dispatcher
  var repo : CouchbaseRepository = _

  var output: HttpResponse = _


  Given("that the customer is logged in to the app") { () =>

  }

  And("the customer posts a suggestion") { () =>
  output = Await.result(Http().singleRequest(HttpRequest(uri = Uri("http://localhost:8080/suggestion/security"), method = HttpMethods.POST, entity = HttpEntity(ContentTypes.`application/json`, """{"activity":"activity","area":"area"}""")).addCredentials(BasicHttpCredentials("nesa","passw1rd"))),3.seconds)


  }

  Then("the suggestion capture application should return (\\d+)$"){ (response:Int) =>
   output.status.intValue() should be (response)
  }

  And("the application should display the new suggestion for that customer"){ () =>

    output.entity.toString should include ("""{"activity":"activity","area":"area"}""")
  }

  And("the application should include the new suggestion"){ () =>
//   output = Http().singleRequest(HttpRequest(uri = Uri("http://localhost:8080/suggestion/security"), method = HttpMethods.GET).addCredentials(BasicHttpCredentials("nesa","passw1rd")))

     println(output)
  }




}
