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

  var output2: HttpResponse = _

  var output3: HttpResponse = _

  val correctCreds = BasicHttpCredentials("nesa","passw1rd")

  val incorrectCreds = BasicHttpCredentials("nessa","wrongpassword")

  val validJson = """{"activity":"activity","area":"area"}"""

  val invalidJson = """{"activ":"activity","are":"area"}"""

  Given("that the customer is logged in to the app") { () =>

  }

  And("the customer posts a suggestion") { () =>
  output = postJson(correctCreds,validJson)

  }

  Then("the application should return (\\d+)$"){ (response:Int) =>

   output.status.intValue() should be (response)
  }

  And("the application should display the new suggestion for that customer"){ () =>
    output.entity.toString should include ("""{"activity":"activity","area":"area"}""")
  }


  When("the customer provides incorrect credentials"){ () =>
    output2 = postJson(incorrectCreds,validJson)
  }

  Then("the application should send (\\d+)$"){ (response:Int) =>
    output2.status.intValue() should be (response)
  }

//    When("the customer sends a suggestion with malformed json"){ ()
//      output3 = postJson(correctCreds,validJson)
//    }
//
//  Then("the application should send back 400"){ ()
//    println("you have" + output3)
//  }




def postJson(basicHttpCredentials: BasicHttpCredentials, json:String): HttpResponse = {
 Await.result(Http().singleRequest(HttpRequest(uri = Uri("http://localhost:8080/suggestion/security"), method = HttpMethods.POST, entity = HttpEntity(ContentTypes.`application/json`, json)).addCredentials(basicHttpCredentials)),3.seconds)
}


}
