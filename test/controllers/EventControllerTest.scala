package controllers


import play.api.libs.json.{JsValue, Json}
import play.api.mvc.{AnyContentAsEmpty, AnyContentAsJson, AnyContentAsText}
import play.api.test.{FakeRequest, PlaySpecification, WithApplication}

class EventControllerTest extends PlaySpecification {
  import data.EventTestData._
  "doing a successful POST on /receiveEvent with correct json for event return a http 200 success" in new WithApplication {
    val jsonValue: JsValue = Json.parse(validEventsAsJson)
    val postOkRequest: FakeRequest[AnyContentAsJson] =
      FakeRequest(POST, "/receiveEvent").withJsonBody(jsonValue).withHeaders(CONTENT_TYPE -> "application/json")

    val Some(result) = route(app, postOkRequest)

    status(result) must equalTo(200)
    contentAsString(result) must equalTo("Event persisted successfully")
  }

  "doing a POST with incorrect json on /receiveEvent should return a http 400 failure" in new WithApplication {
    val jsonValue: JsValue = Json.parse(invalidEventsAsJson)
    val postOkRequest: FakeRequest[AnyContentAsJson] =
      FakeRequest(POST, "/receiveEvent").withJsonBody(jsonValue)
    val Some(result) = route(app, postOkRequest)

    status(result) must equalTo(400)
    contentAsString(result) must equalTo("Invalid events json posted")
  }

  "doing a POST with broken json on /receiveEvent should return a http 400 failure" in new WithApplication {
    val jsonValue: JsValue = Json.parse(invalidEventsAsJson)
    val postOkRequest: FakeRequest[AnyContentAsJson] =
      FakeRequest(POST, "/receiveEvent").withJsonBody(jsonValue)
    val Some(result) = route(app, postOkRequest)

    status(result) must equalTo(400)
    contentAsString(result) must equalTo("Invalid events json posted")
  }

  "doing a POST with empty json on /receiveEvent should return a http 400 failure" in new WithApplication {
    val postOkRequest: FakeRequest[AnyContentAsText] =
      FakeRequest(POST, "/receiveEvent").withTextBody(emptyEventsAsJson)
    val Some(result) = route(app, postOkRequest)

    status(result) must equalTo(400)
    contentAsString(result) must equalTo("Empty events json posted")
  }

  "doing a successful GET to the health endpoint" in new WithApplication {
    val healthGetRequest: FakeRequest[AnyContentAsEmpty.type] =
      FakeRequest(GET, "/health")

    val Some(result) = route(app, healthGetRequest)

    status(result) must equalTo(200)
  }
}
