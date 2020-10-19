package data

import models.Event

object EventTestData {
  val validEventsAsJson: String =
    """[{
      |"timestamp" : 1515609008,
      |"userId" : 1123,
      |"event" : "2 hours of downtime occurred due to the release of version 1.0.5 of the system"
      |}]""".stripMargin

  val emptyEventsAsJson: String =
    """[]""".stripMargin

  val invalidEventsAsJson: String =
    """[{
      |"timestaamp" : 1515609008,
      |"userId" : 1123,
      |"event" : "2 hours of downtime occurred due to the release of version 1.0.5 of the system"
      |}]""".stripMargin

  val brokenEventsAsJson: String =
    """[|"userId" : 1123,
      |"event" : "2 hours of downtime occurred due to the release of version 1.0.5 of the system"
      |}]""".stripMargin

  val events = Seq(
    Event(
      1515609008,
      1123,
      "2 hours of downtime occurred due to the release of version 1.0.5 of the system"
    )
  )

  val noEvents: Seq[Event] = Seq.empty
}
