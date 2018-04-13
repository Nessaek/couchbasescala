package com.nk.planner.routes

import akka.http.scaladsl.server.Route

trait RouteProvider {
  def route: Route
}

