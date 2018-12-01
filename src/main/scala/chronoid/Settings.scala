package chronoid

import java.io.File
import chronoid.OutputFormat.lookup

case class Settings(
  extension:  Option[OutputFormat] = None,
  interval:   Option[Int]          = None,
  filename:   Option[String]       = None,
  target:     Option[File]         = None,
  screenInfo: Option[ScreenInfo]   = None)

object Extension {
  def unapply(args: Args): Option[ErrX[OutputFormat]] =
    Option(args) collect {
      case (x @ ("--extension" | "-e")) :: hd :: tl =>
        OutputFormat(hd) match {
          case None    => Left(errorMsg(hd, x))
          case Some(x) => Right(x -> tl)
        }
    }

  def errorMsg(key: String, arg: String): String = {
    val supportEnum = enum(lookup.keys.toSeq.sorted).getOrElse("")
    s"Argument '$arg' of '$key' is not a supported output format! (Supported formats are: $supportEnum.)"
  }
}

object Interval {
  val Iv = "([0-9]+)".r

  def unapply(args: Args): Option[ErrX[Int]] =
    Option(args) collect {
      case (x @ ("--interval" | "-i")) :: hd :: tl =>
        Option(hd) collect {
          case Iv(x)   => x
        } match {
          case None    => Left(errorMsg(x, hd))
          case Some(x) => Right(x.toInt -> tl)
        }
    }

  def errorMsg(key: String, arg: String): String =
    s"Argument '$arg' of '$key' is not a valid interval! Only positive intervals are allowed!"
}

object Filename {
  def unapply(args: Args): Option[ErrX[String]] =
    Option(args) collect {
      case (x @ ("--filename" | "-f")) :: hd :: tl => Right(hd -> tl)
    }
}

object Target {
  def unapply(args: Args): Option[ErrX[File]] =
    Option(args) collect {
      case (x @ ("--target" | "-t")) :: hd :: tl =>
        val outDirectory = new File(hd)
        val absolutePath = outDirectory.getAbsolutePath

        if (!outDirectory.exists()) {
          Left(doesNotExist(hd, absolutePath))
        } else if (!outDirectory.isDirectory()) {
          Left(isNoDirectory(hd, absolutePath))
        } else {
          Right(outDirectory -> tl)
        }
    }
  
  def doesNotExist(arg: String, absolutePath: String): String =
    s"Target directory '$arg' ('$absolutePath') does not exist!"
  
  def isNoDirectory(arg: String, absolutePath: String): String =
    s"Target directory '$arg' ('$absolutePath') is not a directory!"
}

object PrintScreens {
  def unapply(args: Args): Option[ErrX[Unit]] =
    Option(args) collect {
      case (_ @ "--screeninfo" | "-si") :: tl =>
        Right(() -> tl)
    }
}

object Screen {
  def unapply(args: Args): Option[ErrX[ScreenInfo]] =
    Option(args) collect {
      case (_ @ "--screen" | "-s") :: desc :: tl =>

        val optInfo = Option(desc) flatMap {
          case "all"     => Option(ScreenInfo.All)
          case "default" => Option(ScreenInfo.Default)
          case s =>
            val optionIndex = ScreenInfo.screenDeviceIndexById(s)
            optionIndex.map(ScreenInfo.Single)
          case _ =>
            Option.empty[ScreenInfo]
        }

        optInfo match {
          case Some(info) => Right(info -> tl)
          case _          => Left(s"Invalid screen descriptor '$desc'!")
        }
    }
}
