package mqtt

import com.google.inject.Inject
import eventPB.EventsPB
import exceptions.{FailureConnectingMqttTopicException, SendMqttMessageException}
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import org.eclipse.paho.client.mqttv3.{IMqttToken, MqttClient, MqttDeliveryToken, MqttMessage, MqttTopic}
import play.api.Logging

import scala.util.{Failure, Success, Try}

class MqttPublisher @Inject()(implicit mqttFactory: MqttFactory)
    extends Logging {
  private val brokerUrl = "tcp://localhost:1883"
  private val topic = "event"
  private val mqttDefaultFilePersistence = "/tmp"
  private val persistence = new MqttDefaultFilePersistence(
    mqttDefaultFilePersistence
  )

  implicit private val maybeMqttClient: Option[MqttClient] = buildMqttClient

  def buildMqttClient: Option[MqttClient] = {
    Try(mqttFactory.buildClient(brokerUrl, persistence)) match {
      case Success(client) =>
        logger.info("Client connected successfully")
        Some(client)
      case Failure(failure) =>
        logger.error(
          s"Client connection failure, details: ${failure.getMessage}"
        )
        None
    }
  }

  def connectMqttTopic(
      topic: String = topic
  )(implicit maybeMqttClient: Option[MqttClient] = maybeMqttClient)
    : MqttTopic = {
    Try {
      val client = maybeMqttClient
        .map { mqttClient =>
          if (!mqttClient.isConnected) {
            mqttClient.connect()
            mqttClient
          } else mqttClient
        }
        .getOrElse {
          val message =
            s"Failure connecting to mqtt topic: $topic"
          logger.error(message)
          throw new FailureConnectingMqttTopicException(message)
        }
      client.getTopic(topic)
    } match {
      case Success(topic) =>
        logger.info(s"Connection to topic: $topic successful")
        topic
      case Failure(failure) =>
        val message =
          s"Failure connecting to mqtt topic: $topic, failure: ${failure.getMessage}"
        logger.error(message)

        throw new FailureConnectingMqttTopicException(message)
    }
  }

  def sendMessage(mqttTopic: MqttTopic,
                  eventsPB: EventsPB): MqttDeliveryToken = {
    val message = new MqttMessage(eventsPB.toByteArray)

    val tryDeliveryToken = Try {
      mqttTopic.publish(message)
    }

    tryDeliveryToken match {
      case Success(deliveryToken) =>
        deliveryToken
      case Failure(exception) =>
        val message = s"Failure delivering message ${exception.getMessage}"
        logger.error(message)
        throw new SendMqttMessageException(message)
    }
  }

  def disconnect(): Unit = {
    maybeMqttClient.foreach(client => client.disconnect())
  }
}
