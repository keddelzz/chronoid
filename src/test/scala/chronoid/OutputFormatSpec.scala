package chronoid

import org.scalatest._

class OutputFormatSpec extends FlatSpec with Matchers {

  private val formatsLowercase =
    Seq(
      "bmp",
      "gif",
      "jpg",
      "jpeg",
      "png",
      "wbmp")

  private val formats =
    formatsLowercase ++
    formatsLowercase.map(_.toUpperCase())

  "Valid output formats" should "be supported" in {
    for (format <- formats) {
      val maybeFmt = OutputFormat(format)
      maybeFmt shouldBe a[Some[_]]
      val Some(fmt) = maybeFmt
      fmt.fileExtentsion should be (format.toLowerCase)
    }
  }

  "Invalid output formats" should "not be supported" in {
    for (format <- Seq("abc", "def", "avi", "foo", "bar")) {
      val maybeFmt = OutputFormat(format)
      maybeFmt should be (None)
    }
  }

}
