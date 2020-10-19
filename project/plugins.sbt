logLevel := Level.Warn

resolvers += "Typesafe repository" at "https://repo.typesafe.com/typesafe/releases/"

addSbtPlugin("com.typesafe.play" % "sbt-plugin" % "2.8.0")
addSbtPlugin("com.thesamet" % "sbt-protoc" % "0.99.27")
addSbtPlugin("com.lucidchart" % "sbt-scalafmt" % "1.15")

libraryDependencies += "com.thesamet.scalapb" %% "compilerplugin" % "0.9.4"
