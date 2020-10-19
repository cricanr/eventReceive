package controllers

import data.EventTestData.validEventsAsJson
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import org.scalatestplus.play.guice.GuiceOneAppPerSuite
import play.api.Application
import play.api.http.{HeaderNames, HttpVerbs, Status}
import play.api.inject.bind
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsJson, Results}
import play.api.test._
import services.EventPublishService
import stub.EventPublishServiceFailed

class EventControllerFailureTest
    extends AnyWordSpec
    with Results
    with HeaderNames
    with GuiceOneAppPerSuite
    with Status
    with DefaultAwaitTimeout
    with ResultExtractors
    with HttpVerbs
    with Matchers {

  override def fakeApplication(): Application =
    new GuiceApplicationBuilder()
      .overrides(bind[EventPublishService].to[EventPublishServiceFailed])
      .build()

  "when the internal event publish service fails with a generic failure" should {
    "return a failure" in {
      val jsonValue: JsValue = Json.parse(validEventsAsJson)

      val controller: EventController =
        app.injector.instanceOf[EventController]
      val postFailedRequest: FakeRequest[AnyContentAsJson] =
        FakeRequest(POST, "/receiveEvent")
          .withJsonBody(jsonValue)
          .withHeaders(CONTENT_TYPE -> "application/json")

      val result = controller.receiveEvent()(postFailedRequest.withHeaders())

      status(result) shouldBe (500)
      contentAsString(result) shouldEqual ("Failure occurred while intercepting event. Please try again later")
    }
  }
}
