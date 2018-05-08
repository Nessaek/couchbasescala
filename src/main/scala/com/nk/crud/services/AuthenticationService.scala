package com.nk.crud.services

import akka.actor.ActorSystem
import akka.http.scaladsl.server.directives._
import akka.stream.ActorMaterializer
import com.nk.crud.repo.CouchbaseRepository

import scala.concurrent.Future


class AuthenticationService extends CouchbaseRepository {

  def myUserPassAuthenticator(credentials: Credentials): Future[Option[String]] =
Future {
      credentials match {
        case p@Credentials.Provided(id) if p.verify(sys.env("COUCH_SCALA_PASSWORD")) => Some(id)
        case _ => None
      }

    }
}

