package data

import exceptions.{EventJsonParsingException, FailureConnectingMqttTopicException, SendMqttMessageException}

object EventPublishFailures {
  val jsonParsingException = new EventJsonParsingException(
    "failure decoding events json"
  )

  val failureConnectingTopicException = new FailureConnectingMqttTopicException(
    "failure connecting to topic"
  )

  val failureSendingMessage = new SendMqttMessageException(
    "failure sending message"
  )
}
