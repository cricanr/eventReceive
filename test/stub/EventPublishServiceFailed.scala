package stub

import com.google.inject.Inject
import exceptions.SendMqttMessageException
import mqtt.MqttPublisher
import org.eclipse.paho.client.mqttv3.MqttDeliveryToken
import services.EventPublishService

class EventPublishServiceFailed @Inject()(mqttPublisher: MqttPublisher)
    extends EventPublishService(mqttPublisher = null) {
  override def persist(
    eventsJson: String
  ): Either[Throwable, MqttDeliveryToken] = {
    Left(
      new SendMqttMessageException("failure sending message over mqtt queue")
    )
  }
}
