Application that receives events over http POST
=====
This is a HTTP Play framework REST.API service implemented in Scala that receives events over POST, checks that the JSON is correct
and will afterwards cast these events to Google Protocol Buffer events and send them over a TCP/IP using
MQTT protocol. 


Architecture & code notes: 
======
Communication with the 2nd application (`eventPersist`) is done over: ```tcp://localhost:1883``` on the MQTT topic: `event`
Log information is currently sent to the standard output for all log levels. This can be improved based on requirements.
I am using the Play framework default logger implementation.
Testing is done using the standard `Scalatest` library.
In order to work with Google protocol buffer I am using ScalaPB library. In order for this to work you need to
create `.proto` files, e.g. in my case: `eventPB.proto` and the compiler will automatically create for us case classes and 
all needed code to convert to / from and work them. 

Communication over MQTT is done using a Scala MQTT client. We have: `MQTTPublisher` that handles all direct communication
to the MQTT client code. This is an abstraction layer. 

Events arrive via POST using the `EventController` which will then communicate with `EventPublishService` that 
will send the message to the `MQTTPublisher` which in turn will send them over the queue to be persisted.
The `Event` model is our internal "business" representation of the event. This is currently the same as the event
in GPB format but in other more complex cases it could be different.

For dependency injection I am using Google Guice which comes by default with Play.

Further development:
====
As a minimal solution I have not considered for now some points that should be addressed: 
* docker container with the application
* resiliency features, e.g. `resilience4j` as `hystrix` went to maintenance mode now. Resiliency should be built in the 
MQTT client implementation. We need to think of a retry mechanism and a strategy what to do with events that fail to be
sent over to the `eventPersist` application
* logging should be extended and a logging mechanism to collect logs for further monitoring
* Add more testing, especially integration tests for the 2 applications
* Improve the MQTT architecture to be more flexible
* add configuration entries
* add CI/CD


Rest interface: 
==== 
HTTP Rest.API endpoints: 
We have 2 endpoints:
```
/health
/eventReceive
```
The /health endpoint can be used in the future to check the health of the service
The /eventReceive endpoint accepts POST requests with the JSON payload containing events.
As an example an event looks like: 
```{
   "timestamp" : 1518609008,
   "userId" : 1123,
   "event" : "2 hours of downtime occured due to the release of version 1.0.5 of the
   system"
   }
```

In order to POST events on this endpoint you can use curl, POSTMAN or whatever tool is more convenient to you.
Set `content-type: application/json`


Running the project & prerequisites instructions
===
Prerequisites: 
I am using an MQTT Scala client but we need to have a broker installed. I am using `mosquitto` broker.
In order to set it up, please run the commands based on your system needs:

1) MacOS:
Link: https://subscription.packtpub.com/book/application_development/9781787287815/1/ch01lvl1sec12/installing-a-mosquitto-broker-on-macos

```
      brew install mosquitto
      /usr/local/sbin/mosquitto -c /usr/local/etc/mosquitto/mosquitto.conf

```

Ubuntu: 
Link: https://www.vultr.com/docs/how-to-install-mosquitto-mqtt-broker-server-on-ubuntu-16-04
 
```sudo apt-get update
   sudo apt-get install mosquitto
```

Running the application:
As a build tool I am using `sbt` so we have the following commands using terminal:
```sbt clean compile test run``` this will clean, compile test and run the web application
We can also just pick whichever commands we want based on our needs.

Another option is to use IntelliJ for testing, compiling and running the application


Scalafmt:
======
In order to have well formatted, consistent, easy to maintain code approved by Scala community standards I use Scalafmt.
It is configurable to work within IntelliJ or other IDEs, integrated with your favourite shortcuts and also at build time
when a file is saved code will be reformatted accordingly.
Installation documentation: https://github.com/lucidsoftware/neo-sbt-scalafmt
Useful `sbt` commands to run Scalafmt tasks:
```sbtshell
> scalafmt       # format compile sources
> test:scalafmt  # format test sources
> sbt:scalafmt   # format .sbt source 
```

```
sbt scalafmt
```


Useful links:
======
1) Play framework: https://www.playframework.com/
2) Scala-MQTT-Client: https://github.com/mqtt/mqtt.github.io/wiki/Scala-MQTT-Client
3) MQTT broker: http://mosquitto.org/blog/
4) ScalaPB (Scala Google protocol buffer library): https://scalapb.github.io/scalapbc.html
5) Google protocol buffers: https://developers.google.com/protocol-buffers