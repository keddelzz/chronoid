package chronoid

import org.scalatest._
import chronoid.Chronoid.parseArgs
import chronoid.Chronoid.syntax
import chronoid.OutputFormat._
import java.io.File

class ArgumentsSpec extends FlatSpec with Matchers {

  "Parsing of no arguments" should "yield an empty Settings instance" in {
    parseArgs(List(), Settings()) should be (Right(Settings()))
  }

  "Parsing of invalid arguments" should "fail" in {
    val arg = "--testThisVeryLongArgumentThatDoesntExist"
    parseArgs(List(arg), Settings()) should be (Left(s"Illegal argument '$arg'!"))
  }

  "Parsing of duplicate arguments" should "yield the last" in {
    parseArgs(List("-e", "jpg", "-e", "png"), Settings()) should be
      (Right(Settings(extension = Some(Png))))
    parseArgs(List("-i", "1", "-i", "2"), Settings()) should be
      (Right(Settings(interval = Some(2))))
    parseArgs(List("-f", "bar", "-f", "foo"), Settings()) should be
      (Right(Settings(filename = Some("foo"))))
    parseArgs(List("-t", "src/main", "-t", "src/test"), Settings()) should be
      (Right(Settings(target = Some(new File("src/test")))))
  }

  "Parsing the extension argument" should "work" in {
    val extension: OutputFormat = Jpg

    val extensionArgs = Seq(
      parseArgs(List("-e", extension.fileExtentsion), Settings()),
      parseArgs(List("--extension", extension.fileExtentsion), Settings()))
    for (extensionArg <- extensionArgs) {
      extensionArg should be (Right(Settings(extension = Some(extension))))
    }

    parseArgs(List("-e", "avi"), Settings()) should be
      (Left(s"Argument 'avi' of '-e' is not a supported output format!"))
    parseArgs(List("--extension", "avi"), Settings()) should be
      (Left(s"Argument 'avi' of '--extension' is not a supported output format!"))
  }

  "Parsing the interval argument" should "work" in {
    val interval  = 42

    val intervalArgs = Seq(
      parseArgs(List("-i", interval.toString), Settings()),
      parseArgs(List("--interval", interval.toString), Settings()))
    for (intervalArg <- intervalArgs) {
      intervalArg should be (Right(Settings(interval = Some(interval))))
    }

    parseArgs(List("-i", "foo"), Settings()) should be
      (Left(s"Argument 'foo' of '-i' is not a valid interval!"))
    parseArgs(List("--interval", "foo"), Settings()) should be
      (Left(s"Argument 'foo' of '--interval' is not a valid interval!"))
  }

  "Parsing filename argument" should "work" in {
    val fileName  = "screen.png"

    val nameArgs = Seq(
      parseArgs(List("-f", fileName), Settings()),
      parseArgs(List("--filename", fileName), Settings()))
    for (nameArg <- nameArgs) {
      nameArg should be (Right(Settings(filename = Some(fileName))))
    }
  }

  "Parsing target argument" should "work" in {
    val targetDir = "src"

    val targetArgs = Seq(
      parseArgs(List("-t", targetDir), Settings()),
      parseArgs(List("--target", targetDir), Settings()))
    for (targetArg <- targetArgs) {
      targetArg should be (Right(Settings(target = Some(new File(targetDir)))))
    }

    val barPath = new File("bar").getAbsolutePath
    val buildFile = "build.sbt"
    val buidSbtPath = new File(buildFile).getAbsolutePath

    parseArgs(List("-t", "bar"), Settings()) should be
      (Left(s"Target directory 'bar' ('$barPath') does not exist!"))
    parseArgs(List("--target", "bar"), Settings()) should be
      (Left(s"Target directory 'bar' ('$barPath') does not exist!"))

    parseArgs(List("-t", buildFile), Settings()) should be
      (Left(s"Target directory '$buildFile' ('$buidSbtPath') is not a directory!"))
    parseArgs(List("--target", buildFile), Settings()) should be
      (Left(s"Target directory '$buildFile' ('$buidSbtPath') is not a directory!"))
  }

  "Parsing the help argument" should "work" in {
    val args = Seq(
      parseArgs(List("-h"), Settings()),
      parseArgs(List("--help"), Settings()),

      parseArgs(List("-h", "asdasd"), Settings()),
      parseArgs(List("--help", "asdasd"), Settings()),

      parseArgs(List("-h", "-i", "50"), Settings()),
      parseArgs(List("--help", "-i", "50"), Settings()),

      parseArgs(List("-i", "50", "-h"), Settings()),
      parseArgs(List("-i", "50", "--help"), Settings()))

    for (arg <- args) {
      arg should be (Left(syntax))
    }
  }

}
