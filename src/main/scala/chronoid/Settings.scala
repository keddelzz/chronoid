package chronoid

import java.io.File

case class Settings(
  extension: Option[OutputFormat] = None,
  interval:  Option[Int]          = None,
  filename:  Option[String]       = None,
  target:    Option[File]         = None)

object Extension {
  def unapply(args: Args): Option[ErrX[OutputFormat]] =
    Option(args) collect {
      case (x @ ("--extension" | "-e")) :: hd :: tl =>
        OutputFormat(hd) match {
          case None    => Left(s"Argument '$hd' of '$x' is not a supported output format!")
          case Some(x) => Right(x -> tl)
        }
    }
}

object Interval {
  val Iv = "([0-9]+)".r

  def unapply(args: Args): Option[ErrX[Int]] =
    Option(args) collect {
      case (x @ ("--interval" | "-i")) :: hd :: tl =>
        Option(hd) collect {
          case Iv(x) => x
        } match {
          case None    => Left(s"Argument '$hd' of '$x' is not a valid interval!")
          case Some(x) => Right(x.toInt -> tl)
        }
    }
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
          Left(s"Target directory '$hd' ('$absolutePath') does not exist!")
        } else if (!outDirectory.isDirectory()) {
          Left(s"Target directory '$hd' ('$absolutePath') is not a directory!")
        } else {
          Right(outDirectory -> tl)
        }
    }
}
