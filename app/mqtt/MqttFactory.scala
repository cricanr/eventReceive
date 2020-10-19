package mqtt

import com.google.inject.Inject
import exceptions.CreateMqttClientException
import org.eclipse.paho.client.mqttv3.MqttClient
import org.eclipse.paho.client.mqttv3.persist.MqttDefaultFilePersistence
import play.api.Logging

import scala.util.{Failure, Success, Try}

trait MqttFactory {
  def buildClient(brokerUrl: String,
                  persistence: MqttDefaultFilePersistence): MqttClient
}

class MqttFactoryImpl @Inject() extends MqttFactory with Logging {
  override def buildClient(
      brokerUrl: String,
      persistence: MqttDefaultFilePersistence
  ): MqttClient = {
    Try(new MqttClient(brokerUrl, MqttClient.generateClientId, persistence)) match {
      case Success(client) =>
        client
      case Failure(failure) =>
        val message =
          s"Failure building MQTT client detail: ${failure.getMessage}"
        logger.error(message)
        throw new CreateMqttClientException(message)
    }
  }
}
