package services

import eventPB.EventsPB
import models.Event
import mqtt.MqttPublisher
import org.eclipse.paho.client.mqttv3.{MqttClient, MqttDeliveryToken, MqttTopic}
import org.mockito.ArgumentMatchers.any
import org.mockito.Mockito.when
import org.mockito.MockitoSugar
import org.mockito.MockitoSugar.mock
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec
import play.api.{Logger => UnderlyingLogger}

import scala.util.Try

class EventPublishServiceTest
    extends AnyWordSpec
    with MockitoSugar
    with Matchers {

  import EventPublishServiceTest._
  import data.EventPublishFailures._
  import data.EventTestData._

  "The EventPublishService" when {
    "calling persist successfully" should {
      "return a delivery token" in {
        val mqttPublisherMock = mock[MqttPublisher]

        val mqttDeliveryToken = new MqttDeliveryToken()
        when(mqttPublisherMock.sendMessage(any[MqttTopic], any[EventsPB]))
          .thenReturn(mqttDeliveryToken)

        val (loggerMock, eventPublishService) =
          eventPublishServiceWithMockLogger(mqttPublisherMock)

        eventPublishService.persist(validEventsAsJson.stripMargin) shouldBe Right(
          mqttDeliveryToken
        )

        verify(loggerMock).info("Sending events to MQTT queue")
      }
    }

    "calling persist with a JSON decoding failure" should {
      "return a failed result" in {
        val mqttPublisherMock = mock[MqttPublisher]
        val tryJsonDecodingEvents =
          Try(Event.decodeEvents(invalidEventsAsJson.stripMargin))
        val eventPublishService = new EventPublishService(mqttPublisherMock)
        implicit val mqttClientMock: MqttClient = mock[MqttClient]
        val expectedFailure =
          tryJsonDecodingEvents.failed.get.getClass.getSimpleName
        val actualFailure = eventPublishService
          .persist(invalidEventsAsJson.stripMargin)
          .left
          .get
          .getClass
          .getSimpleName

        actualFailure shouldBe (expectedFailure)
      }
    }

    "calling persist with a failure when connecting to topic" should {
      "return a failed result" in {
        val mqttPublisherMock = mock[MqttPublisher]
        val eventPublishService = new EventPublishService(mqttPublisherMock)

        when(mqttPublisherMock.sendMessage(any[MqttTopic], any[EventsPB]))
          .thenThrow(failureConnectingTopicException)

        eventPublishService.persist(validEventsAsJson.stripMargin) shouldBe Left(
          failureConnectingTopicException
        )
      }
    }

    "calling persist with a failure when sending the message" should {
      "return a failed result" in {
        val mqttPublisherMock = mock[MqttPublisher]
        val eventPublishService = new EventPublishService(mqttPublisherMock)

        when(mqttPublisherMock.sendMessage(any[MqttTopic], any[EventsPB]))
          .thenThrow(failureSendingMessage)

        eventPublishService.persist(validEventsAsJson.stripMargin) shouldBe Left(
          failureSendingMessage
        )
      }
    }
  }
}

object EventPublishServiceTest {
  def eventPublishServiceWithMockLogger(
    mqttPublisher: MqttPublisher
  ): (UnderlyingLogger, EventPublishService) = {
    val mockLogger = mock[UnderlyingLogger]
    when(mockLogger.isInfoEnabled).thenReturn(true)
    val eventPublishService = new EventPublishService(
      mqttPublisher = mqttPublisher
    ) {
      override val logger = mockLogger
    }

    (mockLogger, eventPublishService)
  }
}
