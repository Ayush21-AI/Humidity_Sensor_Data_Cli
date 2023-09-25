ThisBuild / version := "0.1.0-SNAPSHOT"

ThisBuild / scalaVersion := "2.13.12"

lazy val root = (project in file("."))
  .settings(
    name := "Humidity_Sensor_Data_Cli"
  )

lazy val catsVersion = "2.10.0"

libraryDependencies += "org.typelevel" %% "cats-core" % catsVersion

libraryDependencies += "org.typelevel" %% "cats-effect" % "3.6-0142603"

libraryDependencies += "org.typelevel" %% "cats-laws" % catsVersion

libraryDependencies += "org.typelevel" %% "cats-kernel" % catsVersion

libraryDependencies += "org.typelevel" %% "cats-effect" % "3.6-0142603"

libraryDependencies += "co.fs2" %% "fs2-core" % "3.10-4b5f50b"

libraryDependencies += "co.fs2" %% "fs2-io" % "3.10-4b5f50b"

libraryDependencies += "co.fs2" %% "fs2-reactive-streams" % "3.10-4b5f50b"

libraryDependencies += "co.fs2" %% "fs2-scodec" % "3.10-4b5f50b"

libraryDependencies += "org.scalatest" %% "scalatest" % "3.2.15" % Test










