package chronoid

import scala.annotation.tailrec
import javax.imageio.ImageIO
import java.io.File
import java.awt.{ Toolkit, Dimension, Rectangle, Robot }

object Chronoid {

  def main(args: Array[String]): Unit = {
    val settings  = parseArgs(args.toList, Settings())
    val validated = settings.right.flatMap(validateArgs)
    validated.fold(error(_, "", syntax), execute)
  }

  val syntax =
    Seq(
      "syntax: chronoid <filename>.<extension> <interval> <target>",
      "where",
      "\t-e | --extension\t\tFile extention for output file",
      "\t-i | --interval\t\t\tInterval between two screenshots",
      "\t-f | --filename\t\t\tSuffix of the name, which is used for screenshots",
      "\t-t | --target\t\t\tTarget directory",
      "",
      "\tOptions can be given in any order.").mkString("\n")

  private def execute(settings: Settings): Unit = {
    val Settings(Some(ext), Some(inv), Some(name), Some(dst)) = settings

    def filename(n: Int): String =
      s"$name-$n.${ext.fileExtentsion}"

    @tailrec
    def go(cnt: Int): Unit = {
      val fname = filename(cnt)
      val screenSize = Toolkit.getDefaultToolkit().getScreenSize()
      val image = new Robot().createScreenCapture(new Rectangle(screenSize))
      val file = new File(dst, fname)
      ImageIO.write(image, ext.fileExtentsion, file)
      println(s"Created file '${file.getAbsolutePath}'!")
      Thread.sleep(inv)
      go(cnt + 1)
    }

    Runtime.getRuntime().addShutdownHook(new Thread() {
      override def run() = {
        val nameTemplate = "\"" + s"$name-${"%d"}.${ext.fileExtentsion}" + "\""
        println("Screenshots can be combined to a timelapse with e.g.")
        println(s"ffmpeg -start_number 0 -r 10 -i $nameTemplate -q:v 1 -b:v 1500k $name.mp4")
        println()
      }
    })

    go(0)
  }

  final def validateArgs(settings: Settings): Err[Settings] = {
    val Settings(ext, inv, fname, dst) = settings

    type OutF = OutputFormat
    lazy val invErr:   Err[Int]    = Left("No interval was specified!")
    lazy val dstErr:   Err[File]   = Left("No output directory was specified!")
    lazy val extErr:   Err[OutF]   = Left("No file extension was specified!")
    lazy val fnameErr: Err[String] = Left("No filename was specified!")

    for {
      _ <- inv.fold(invErr)(Right(_)).right
      _ <- dst.fold(dstErr)(Right(_)).right
      _ <- fname.fold(fnameErr)(Right(_)).right
      _ <- ext.fold(extErr)(Right(_)).right
    } yield settings
  }

  final def parseArgs(args: Args, settings: Settings): Err[Settings] =
    args match {
      case Extension(ex)          => ex.right flatMap {
        case (ext, rest)          => parseArgs(rest, settings.copy(extension = Some(ext)))
      }
      case Interval(ix)           => ix.right flatMap {
        case (iv, rest)           => parseArgs(rest, settings.copy(interval = Some(iv)))
      }
      case Filename(nx)           => nx.right flatMap {
        case (name, rest)         => parseArgs(rest, settings.copy(filename = Some(name)))
      }
      case Target(tx)             => tx.right flatMap {
        case (out, rest)          => parseArgs(rest, settings.copy(target = Some(out)))
      }
      case ("-h" | "--help") :: _ => Left(syntax)
      case hd :: _                => Left(s"Illegal argument '$hd'!")
      case _                      => Right(settings)
    }

}