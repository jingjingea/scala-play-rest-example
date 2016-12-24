import play.sbt.PlayImport._

name := """scala-play-rest-example"""

version := "1.0-SNAPSHOT"

lazy val root = (project in file(".")).enablePlugins(PlayScala)

scalaVersion := "2.11.6"

libraryDependencies ++= Seq(
  jdbc,
  cache,
  ws,
  // postgres jdbc
  "org.postgresql" % "postgresql" % "9.4-1201-jdbc41",
  // slick
  "com.typesafe.slick" %% "slick" % "3.1.1",
  "com.typesafe.slick" %% "slick-hikaricp" % "3.1.1",
  specs2 % Test
)
resolvers += "scalaz-bintray" at "https://dl.bintray.com/scalaz/releases"
libraryDependencies += "org.mockito" % "mockito-core" % "1.9.5" % "test"
