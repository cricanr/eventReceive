package mqtt

import eventPB.EventsPB
import eventPB.EventsPB.EventPB
import exceptions.{
  CreateMqttClientException,
  FailureConnectingMqttTopicException,
  SendMqttMessageException
}
import org.eclipse.paho.client.mqttv3.internal.ClientComms
import org.eclipse.paho.client.mqttv3.{MqttClient, MqttDeliveryToken, MqttTopic}
import org.mockito.ArgumentMatchers.any
import org.mockito.MockitoSugar
import org.scalatest.PrivateMethodTester
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

class MqttPublisherTest
    extends AnyWordSpec
    with PrivateMethodTester
    with Matchers
    with MockitoSugar {

  "The MqttPublisher" when {
    implicit val mqttFactoryMock: MqttFactory = mock[MqttFactory]
    implicit val mqttClientMock: MqttClient = mock[MqttClient]

    "building an mqtt client successfully" should {
      "return a valid client" in {
        when(mqttFactoryMock.buildClient(any(), any()))
          .thenReturn(mqttClientMock)

        val mqttPublisher = new MqttPublisher()(mqttFactoryMock)

        mqttPublisher.buildMqttClient shouldBe (Some(mqttClientMock))
      }
    }

    "building an mqtt client when an internal exception occurs" should {
      "return no client" in {
        implicit val mqttFactoryFailsMock: MqttFactory = mock[MqttFactory]
        when(mqttFactoryFailsMock.buildClient(any(), any()))
          .thenThrow(new CreateMqttClientException("failure creating client"))

        val mqttPublisher = new MqttPublisher()(mqttFactoryFailsMock)
        mqttPublisher.buildMqttClient shouldBe (None)
      }
    }

    "connecting to a valid topic" should {
      "return a valid topic" in {
        when(mqttFactoryMock.buildClient(any(), any()))
          .thenReturn(mqttClientMock)
        val mqttPublisher = new MqttPublisher()

        doNothing.when(mqttClientMock).connect()
        val clientCommsMock = mock[ClientComms]
        val expectedTopic = new MqttTopic("happyTopic", clientCommsMock)
        when(mqttClientMock.getTopic("myTopic")).thenReturn(expectedTopic)
        mqttPublisher.connectMqttTopic("myTopic") shouldBe (expectedTopic)
        verify(mqttClientMock).connect()
      }
    }

    "connecting to a valid topic with an empty client" should {
      "return a valid topic should try connecting again" in {
        when(mqttFactoryMock.buildClient(any(), any()))
          .thenReturn(mqttClientMock)
        val mqttPublisher = new MqttPublisher()

        doNothing.when(mqttClientMock).connect()
        val clientCommsMock = mock[ClientComms]
        val expectedTopic = new MqttTopic("happyTopic", clientCommsMock)
        when(mqttClientMock.getTopic("myTopic")).thenReturn(expectedTopic)
        mqttPublisher.connectMqttTopic("myTopic")(Some(mqttClientMock)) shouldBe (expectedTopic)

        verify(mqttClientMock, times(2)).connect()
      }
    }

    "connecting to an invalid topic" should {
      "return a FailureConnectingMqttTopic failure" in {
        when(mqttFactoryMock.buildClient(any(), any()))
          .thenReturn(mqttClientMock)

        val mqttPublisher = new MqttPublisher()

        doNothing.when(mqttClientMock).connect()
        when(mqttClientMock.getTopic("myTopic"))
          .thenThrow(new Exception("bam!"))

        intercept[FailureConnectingMqttTopicException] {
          mqttPublisher.connectMqttTopic("myTopic")
        }
      }
    }

    "sending a message over a valid topic" should {
      "return the delivery token" in {
        when(mqttFactoryMock.buildClient(any(), any()))
          .thenReturn(mqttClientMock)

        val mqttPublisher = new MqttPublisher()
        val eventsPB = new EventsPB(Seq(EventPB(11212, 12, "my event")))
        val mqttTopicMock = mock[MqttTopic]
        val deliveryTokenMock = mock[MqttDeliveryToken]
        when(mqttTopicMock.publish(any())).thenReturn(deliveryTokenMock)

        mqttPublisher.sendMessage(mqttTopicMock, eventsPB) shouldBe (deliveryTokenMock)

      }
    }

    "sending a message over an invalid topic" should {
      "return a failure" in {
        when(mqttFactoryMock.buildClient(any(), any()))
          .thenReturn(mqttClientMock)

        val mqttPublisher = new MqttPublisher()
        val eventsPB = new EventsPB(Seq(EventPB(11212, 12, "my event")))
        val mqttTopicMock = mock[MqttTopic]
        val deliveryTokenMock = mock[MqttDeliveryToken]
        when(mqttTopicMock.publish(any())).thenThrow(new Exception("bam!"))

        intercept[SendMqttMessageException] {
          mqttPublisher.sendMessage(mqttTopicMock, eventsPB) shouldBe (deliveryTokenMock)
        }
      }
    }
  }
}
