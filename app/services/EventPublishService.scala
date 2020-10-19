package services

import com.google.inject.Inject
import models.Event
import mqtt.MqttPublisher
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken
import play.api.Logging

import scala.util.{Failure, Success, Try}

trait IEventPublishService {
  def persist(eventsJson: String): Either[Throwable, MqttDeliveryToken]
}

class EventPublishService @Inject()(mqttPublisher: MqttPublisher)
    extends Logging {
  def persist(eventsJson: String): Either[Throwable, MqttDeliveryToken] = {
    Try {
      val events = Event.decodeEvents(eventsJson)
      val eventsAsProtoBuf = Event.toEventsPB(events)
      val mqttTopic = mqttPublisher.connectMqttTopic()
      logger.info(s"Sending events to MQTT queue")
      mqttPublisher.sendMessage(mqttTopic, eventsAsProtoBuf)
    } match {
      case Success(deliveryToken) => Right(deliveryToken)
      case Failure(exception)     => Left(exception)
    }
  }
}
