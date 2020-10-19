package controllers

import play.api.test.{FakeRequest, PlaySpecification, WithApplication}

class HealthControllerTest extends PlaySpecification {
  "doing a successful GET on /health endpoint should return a http 200 success" in new WithApplication {
    val Some(result) = route(
      app,
      FakeRequest(GET, "/health").withHeaders(
        CONTENT_TYPE -> "application/json"
      )
    )

    status(result) must equalTo(200)
    contentAsString(result) must equalTo("Ok")
  }
}
