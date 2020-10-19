name := "eventReceive"
 
version := "1.0" 
      
lazy val `eventreceive` = (project in file(".")).enablePlugins(PlayScala)

resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
      
resolvers += "Akka Snapshot Repository" at "https://repo.akka.io/snapshots/"
      
scalaVersion := "2.12.2"

val circeVersion = "0.12.3"

libraryDependencies ++= Seq(
  ws,
  specs2 % Test,
  guice,
  "com.geirsson" %% "scalafmt-core" % "1.5.1",
  "io.circe" %% "circe-core" % circeVersion,
  "io.circe" %% "circe-generic" % circeVersion,
  "io.circe" %% "circe-parser" % circeVersion,
  "org.scalatest" %% "scalatest" % "3.1.0" % Test,
  "org.scalatestplus.play" %% "scalatestplus-play" % "5.0.0" % Test,
  "org.mockito" % "mockito-scala_2.12" % "1.10.4",
  "com.thesamet.scalapb" %% "scalapb-runtime" % scalapb.compiler.Version.scalapbVersion % "protobuf",
  "org.eclipse.paho" % "org.eclipse.paho.client.mqttv3" % "1.2.2"
)

PB.targets in Compile := Seq(
  scalapb.gen() -> (sourceManaged in Compile).value
)