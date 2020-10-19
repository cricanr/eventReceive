package models

import eventPB.EventsPB
import eventPB.EventsPB.EventPB
import exceptions.EventJsonParsingException
import io.circe._
import io.circe.generic.semiauto._
import io.circe.parser.decode
import play.api.Logging

case class Event(timestamp: Long, userId: Int, event: String)

object Event extends Logging {
  implicit val eventDecoder: Decoder[Event] = deriveDecoder

  def decodeEvents(eventsJson: String): Seq[Event] = {
    decode[Seq[Event]](eventsJson) match {
      case Right(events) =>
        events
      case Left(failure) =>
        val message =
          s"Failure parsing events json, detail: ${failure.getMessage}"
        logger.error(message)
        throw new EventJsonParsingException(message)
    }
  }

  def toEventsPB(events: Seq[Event]): EventsPB = {
    EventsPB(
      events.map(event => EventPB(event.timestamp, event.userId, event.event))
    )
  }
}
