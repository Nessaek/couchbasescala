package com.nk.crud.model

import com.softwaremill.session.{SessionSerializer, SingleValueSessionSerializer}

import scala.util.Try


case class Session(username: String)


object Session {
  implicit def serializer: SessionSerializer[Session, String] =
    new SingleValueSessionSerializer(_.username,
      (un: String) =>
        Try {
          println(un)
          Session(un)
        })
}
