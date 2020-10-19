package controllers

import akka.stream.Materializer
import com.google.inject.Inject
import javax.inject.Singleton
import play.api.mvc._

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class HealthController @Inject()(cc: ControllerComponents)(
    implicit ec: ExecutionContext,
    mat: Materializer)
    extends AbstractController(cc) {

  def health: Action[AnyContent] = Action.async { implicit request =>
    Future.successful(Ok("Ok"))
  }
}
