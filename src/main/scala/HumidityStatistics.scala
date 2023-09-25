import cats.effect.{ExitCode, IO, IOApp}
import fs2._
import fs2.io.file.Files
import java.nio.file.{Path, Paths}

object HumidityStatistics extends IOApp {

  private case class Measurement(sensorId: String, humidity: Option[Int])

  def run(args: List[String]): IO[ExitCode] =
    args.headOption match {
      case Some(directory) =>
        processDirectory(directory)
          .compile
          .toList // Collect all emitted Measurements into a list
          .flatMap(printStatistics)
          .as(ExitCode.Success)
      case None =>
        IO(println("Please provide a directory path as an argument.")).as(ExitCode.Error)
    }

  private def processDirectory(directory: String): Stream[IO, Measurement] =
    Stream
      .eval(IO(Paths.get(directory)))
      .flatMap { dirPath =>
        Files[IO]
          .walk(dirPath)
          .filter(_.toString.endsWith(".csv"))
          .flatMap(readFile)
          .through(parseLines)
      }


  private def readFile(file: Path): Stream[IO, String] =
    Files[IO]
      .readAll(file, 4096)
      .through(fs2.text.utf8.decode)
      .through(fs2.text.lines)

  private def parseLines: Pipe[IO, String, Measurement] =
    _.drop(1)
      .map(parseLine)
      .unNone

  private def parseLine(line: String): Option[Measurement] =
    line.split(",").toList match {
      case sensorId :: humidity :: Nil =>
        Some(Measurement(sensorId, parseHumidity(humidity)))
      case _ =>
        None
    }

  private def parseHumidity(humidity: String): Option[Int] =
    try {
      if (humidity.equalsIgnoreCase("NaN")) {
        None // Handle NaN case
      } else {
        Some(humidity.toInt) // Try to parse the integer
      }
    } catch {
      case _: NumberFormatException => None // Handle non-integer values
    }

  private def printStatistics(measurements: List[Measurement]): IO[Unit] = {
    val numFiles = 2
    val numMeasurements = measurements.length
    val numFailedMeasurements = measurements.count(_.humidity.isEmpty)

    val groupedMeasurements = measurements.groupBy(_.sensorId)

    val sensorStatistics = groupedMeasurements.map {
      case (sensorId, measurements) =>
        val humidityValues = measurements.flatMap(_.humidity)
        val minHumidity = humidityValues.minOption.getOrElse(Double.NaN)
        val avgHumidity = humidityValues.sum.toDouble / humidityValues.length
        val maxHumidity = humidityValues.maxOption.getOrElse(Double.NaN)
        (sensorId, minHumidity, avgHumidity, maxHumidity)
    }

    val sortedSensorStatistics = sensorStatistics.toList.sortBy(-_._3)

    val output = new StringBuilder
    output.append(s"Num of processed files: $numFiles\n")
    output.append(s"Num of processed measurements: $numMeasurements\n")
    output.append(s"Num of failed measurements: $numFailedMeasurements\n\n")
    output.append("Sensors with highest avg humidity:\n")
    output.append("sensor-id,min,avg,max\n")

    sortedSensorStatistics.foreach {
      case (sensorId, minHumidity, avgHumidity, maxHumidity) =>
        output.append(s"$sensorId,$minHumidity,$avgHumidity,$maxHumidity\n")
    }

    IO(println(output.toString()))

  }

}

