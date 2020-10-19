package exceptions

class EventJsonParsingException(message: String) extends Throwable
class CreateMqttClientException(message: String) extends Throwable
class FailureConnectingMqttTopicException(message: String) extends Throwable
class SendMqttMessageException(message: String) extends Throwable
