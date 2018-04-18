import Main.future
import akka.actor._
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import akka.util.Timeout
import com.typesafe.config.{Config, ConfigFactory}
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Directives.{complete, get, pathPrefix}
import akka.stream.ActorMaterializer
import com.nk.planner.repo.{CouchDriver, CouchbaseRepository, FutureRepository, PlannerRepository}
import com.nk.planner.routes.PlannerRoute
import com.typesafe.config.ConfigFactory

import scala.util.{Failure, Success}
import scala.concurrent.duration._
import scala.concurrent.{Await, ExecutionContext, Future}


object Main extends App {
  implicit val sys: ActorSystem = ActorSystem("planner")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = sys.dispatcher

  val repository: PlannerRepository = new PlannerRepository()
  val routeClass: PlannerRoute = new PlannerRoute
  val future: FutureRepository = new FutureRepository


  Http().bindAndHandle(routeClass.plannerRoute, "localhost", 8080) map { binding =>
    println(s"REST interface bound to ${binding.localAddress}") } recover { case ex =>
    println(s"REST interface could not bind to port", ex.getMessage)
  }

}
