package com.nk.crud.steps

import akka.actor.ActorSystem

import scala.util.{Failure, Success}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Authorization, BasicHttpCredentials}
import akka.stream.ActorMaterializer
import com.nk.crud.model.Activity
import com.nk.crud.repo.CouchbaseRepository
import cucumber.api.Scenario
import cucumber.api.scala.{EN, ScalaDsl}

import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import org.scalatest.Matchers._



class StepDefinitions extends ScalaDsl with EN {

  implicit val system = ActorSystem("ReactiveCouchbaseSystem")
  implicit val materializer = ActorMaterializer.create(system)
  implicit val ec = system.dispatcher
  var repo: CouchbaseRepository = _

  var output0: HttpResponse = _
  var output1: HttpResponse = _

  var output2: HttpResponse = _

  var output3: HttpResponse = _

  var output4: HttpResponse = _

  var output5: HttpResponse = _

  var output6: HttpResponse = _

  val correctCreds = BasicHttpCredentials("nesa", "passw1rd")

  val incorrectCreds = BasicHttpCredentials("nessa", "wrongpassword")

  val validJson = """{"id":"1","activity":"activity","area":"area"}"""

  val invalidJson = """{"id":"1","activ":"activity","are":"area"}"""
x
  Before(){_: Scenario ⇒
  deleteAll()
  }

  Given("that the customer is logged in to the app") { () =>

  }


  And("the database is empty") { () =>
  findAll().toString() should include("application/json,[]")
  }

  And("^the customer posts a suggestion$") { () =>
   output1 = postJson(correctCreds, validJson)

  }

  Then("the application should return (\\d+)$") { (response: Int) =>
  output1.status.intValue() should be(response)
  }

  And("the application should display the new suggestion for that customer") { () =>
  output1.entity.toString should include("""{"id":"1","activity":"activity","area":"area"}""")
  }

  And("the application should have the suggestion available") { () =>
  Thread.sleep(5000)
  findAll().toString() should include("""{"id":"1","activity":"activity","area":"area"}""")
  }

  When("the customer updates the added suggestion") { () =>
      output2 = updateOne("1","""{"id":"1","activity":"updatedactivity","area":"updatedarea"}""")
  }

  Then("the API should send back (\\d+)$"){ (response: Int) =>
    output2.status.intValue() should be (response)

  }

  When("the customer tries to update the added suggestion with an invalid id"){ () =>
    output2 = updateOne("2","""{"id":"1","activity":"updatedactivity","area":"updatedarea"}""")
  }

  Then("the api should return a 404 status") {(response:Int) =>

    output2.status.intValue() should be (response)

  }


  When("the customer tries to delete the item"){ () =>
  output3 = deleteOne("1")

  }

  Then("the API should return (\\d+)$"){ (response: Int) =>
  output3.status.intValue() should be (response)
  }


  When("the customer tries to delete an item with an invalid id"){ () =>
   output4 = deleteOne("2")
  }

  Then("the API should send back a (\\d+) response"){ (response: Int) =>
    output4.status.intValue() should be (response)
  }



  When("the customer provides incorrect credentials"){ () =>

  }

  And("the customer tries to post a suggestion"){() =>
  output5 = postJson(incorrectCreds,validJson)

  }

  Then("the application should send (\\d+)$"){ (response:Int) =>
  output5.status.intValue() should be (response)
  }






  When("^the customer posts a suggestion with malformed json$"){ ()
    output6 = postJson(correctCreds,invalidJson)
  }

  Then("the application should send back (\\d+)$"){ (response:Int) =>
  output6.status.intValue() should be (response)
  }



def postJson(basicHttpCredentials: BasicHttpCredentials, json:String): HttpResponse = {
 Await.result(Http().singleRequest(HttpRequest(uri = Uri("http://localhost:8080/suggestion/security"), method = HttpMethods.POST, entity = HttpEntity(ContentTypes.`application/json`, json)).addCredentials(basicHttpCredentials)),4.seconds)
}

  def updateOne(id:String,json:String): HttpResponse = {
    Await.result(Http().singleRequest(HttpRequest(uri = Uri(s"http://localhost:8080/suggestion/security/$id"), method = HttpMethods.PUT, entity = HttpEntity(ContentTypes.`application/json`, json)).addCredentials(correctCreds)),4.seconds)
  }

  def deleteAll(): HttpResponse = {
    Await.result(Http().singleRequest(HttpRequest(uri = Uri("http://localhost:8080/suggestion/security"), method = HttpMethods.DELETE).addCredentials(correctCreds)),4.seconds)
  }

  def deleteOne(id:String): HttpResponse = {
    Await.result(Http().singleRequest(HttpRequest(uri = Uri(s"http://localhost:8080/suggestion/security/$id"), method = HttpMethods.DELETE).addCredentials(correctCreds)),4.seconds)
  }


  def findAll(): HttpResponse = {
    Await.result(Http().singleRequest(HttpRequest(uri = Uri("http://localhost:8080/suggestion/security"), method = HttpMethods.GET).addCredentials(correctCreds)),4.seconds)
  }

}
