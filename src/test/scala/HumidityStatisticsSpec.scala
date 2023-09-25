import org.scalatest.concurrent.ScalaFutures
import cats.effect.unsafe.implicits.global
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

import java.io.{ByteArrayOutputStream, PrintStream}


class HumidityStatisticsSpec extends AnyWordSpec with Matchers with ScalaFutures {

  // Helper method to capture printed output
  def captureOutput(block: => Unit): String = {
    val outputStream = new ByteArrayOutputStream()
    Console.withOut(new PrintStream(outputStream)) {
      block
    }
    outputStream.toString
  }

  // Define test directories
  val validInputDir = "src/test/resources/valid-input"
  val invalidInputDir = "src/test/resources/invalid-input"

  "HumidityStatistics" should {
    "calculate statistics for valid input files" in {
      val printedOutput = captureOutput(HumidityStatistics.run(List(validInputDir)).unsafeRunSync())

      // Define your expected output
      val expectedOutput =
        """Num of processed files: 2
          |Num of processed measurements: 8
          |Num of failed measurements: 3
          |Sensors with highest avg humidity:
          |sensor-id,min,avg,max
          |s1,10,54.0,98
          |s2,78,82.0,88
          |s3,NaN,NaN,NaN
          |sensor-id,NaN,NaN,NaN
          |""".stripMargin

      // Check if the printed output matches the expected output
      printedOutput should include(expectedOutput)
    }
  }
}
