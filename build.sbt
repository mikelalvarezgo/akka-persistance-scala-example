name := "akka-sample-persistence-scala"

version := "2.4.11"

scalaVersion := "2.11.8"
val casbahV = "3.1.1"
val sprayVersion = "1.2.2"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % "2.4.11",
  "com.typesafe.akka" %% "akka-persistence" % "2.4.11",
  "org.iq80.leveldb"            % "leveldb"          % "0.7",
  "org.fusesource.leveldbjni"   % "leveldbjni-all"   % "1.8",
  "org.mongodb" %% "casbah-commons" % casbahV,
  "org.mongodb" %% "casbah-core" % casbahV,
  "org.mongodb" %% "casbah-query" % casbahV,
  "io.spray" % "spray-json_2.11" % "1.3.2"
)

licenses := Seq(("CC0", url("http://creativecommons.org/publicdomain/zero/1.0")))
