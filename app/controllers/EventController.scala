package controllers

import akka.stream.Materializer
import com.google.inject.Inject
import exceptions.EventJsonParsingException
import javax.inject.Singleton
import play.api.Logging
import play.api.mvc.{
  AbstractController,
  Action,
  AnyContent,
  ControllerComponents
}
import services.EventPublishService

import scala.concurrent.{ExecutionContext, Future}

@Singleton
class EventController @Inject()(cc: ControllerComponents)(
    eventService: EventPublishService
)(implicit ec: ExecutionContext, mat: Materializer)
    extends AbstractController(cc)
    with Logging {
  def receiveEvent: Action[AnyContent] = Action.async { implicit request =>
    request.body.asJson
      .map { json =>
        eventService.persist(json.toString()) match {
          case Right(_) =>
            val message = s"Event persisted successfully"
            logger.info(message)

            Future.successful(Ok(message))
          case Left(_: EventJsonParsingException) =>
            val message = "Invalid events json posted"
            logger.error(message)

            Future.successful(BadRequest(message))
          case Left(_: Throwable) =>
            val message =
              "Failure occurred while intercepting event. Please try again later"
            logger.info(message)

            Future.successful(InternalServerError(message))
        }
      }
      .getOrElse {
        val message = "Empty events json posted"
        Future.successful(BadRequest(message))
      }
  }
}
