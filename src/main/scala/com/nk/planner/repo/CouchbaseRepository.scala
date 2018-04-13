package com.nk.planner.repo

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.stream.scaladsl.Sink
import com.typesafe.config.{Config, ConfigFactory}
import org.reactivecouchbase.rs.scaladsl.{N1qlQuery, ReactiveCouchbase}
import play.api.libs.json.Json

object CouchbaseRepository extends CouchbaseRepository

class CouchbaseRepository extends App {

  def findAll(): N1qlQuery =
   N1qlQuery("SELECT * FROM planner WHERE area=`area`")


}

object CouchDriver extends CouchbaseRepository {

  val system = ActorSystem("ReactiveCouchbaseSystem")

  implicit val materializer = ActorMaterializer.create(system)
  implicit val ec = system.dispatcher


  val driver = ReactiveCouchbase(ConfigFactory.parseString(
    """
      |buckets {
      |  default {
      |    name = "planner"
      |    hosts = ["127.0.0.1"]
      |    authentication = {
      |    username = "Administrator"
      |    password = "password"
      |    }
      |  }
      |}
    """.stripMargin))



   var plannerBucket = driver.bucket("default")

  def init = {
    plannerBucket
  }

  //FIXME
  def test():Unit = {
    plannerBucket.search(N1qlQuery("SELECT * FROM planner WHERE area=`area`")).asSeq

  }


}
