package com.nk.crud.services

import akka.http.scaladsl.server.directives._
import com.nk.crud.repo.CouchbaseRepository

import scala.concurrent.Future


class AuthenticationService extends CouchbaseRepository {

  def myUserPassAuthenticator(credentials: Credentials): Future[Option[String]] =
Future {
      credentials match {
        case p@Credentials.Provided(id) if p.verify("passw1rd") => Some(id)
        case _ => None
      }

    }
}

