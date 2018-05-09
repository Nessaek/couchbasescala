
import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.nk.crud.routes.CouchRoute
import scala.concurrent.ExecutionContext

object Main extends App {
  implicit val sys: ActorSystem = ActorSystem("planner")
  implicit val mat: ActorMaterializer = ActorMaterializer()
  implicit val ec: ExecutionContext = sys.dispatcher

  val routeClass: CouchRoute = new CouchRoute


  Http().bindAndHandle(routeClass.crudRoute, "0.0.0.0", 8080) map { binding =>
    println(s"REST interface bound to ${binding.localAddress}") } recover { case ex =>
    println(s"REST interface could not bind to port", ex.getMessage)
  }

}



