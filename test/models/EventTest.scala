package models

import eventPB.EventsPB
import eventPB.EventsPB.EventPB
import exceptions.EventJsonParsingException
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class EventTest extends AnyWordSpec with Matchers {
  import data.EventTestData._

  "The Event" when {
    "converting valid events json to a list of Events" should {
      "return a valid list of events" in {
        Event.decodeEvents(validEventsAsJson) shouldBe events
      }
    }

    "converting an empty events json to a list of Events" should {
      "return an empty list of events" in {
        Event.decodeEvents(emptyEventsAsJson) shouldBe Seq.empty
      }
    }

    "converting an invalid events json to a list of Events" should {
      "return a failure" in {
        an[EventJsonParsingException] should be thrownBy Event.decodeEvents(
          invalidEventsAsJson
        )
      }
    }

    "casting events to protobuf Events" should {
      "return protobuf events" in {
        Event.toEventsPB(events) shouldBe EventsPB(Seq(EventPB(
          1515609008,
          1123,
          "2 hours of downtime occurred due to the release of version 1.0.5 of the system"
        )))
      }
    }

    "casting no events to protobuf Events" should {
      "return protobuf events" in {
        Event.toEventsPB(noEvents) shouldBe EventsPB()
      }
    }
  }
}