package chronoid

import scala.annotation.tailrec
import javax.imageio.ImageIO
import java.io.File
import java.awt._
import java.awt.image.BufferedImage

object Chronoid {

  def main(args: Array[String]): Unit = {
    val settings  = parseArgs(args.toList, Settings())
    val validated = settings.right.flatMap(validateArgs)
    validated.fold(error(_, "", help(false)), execute)
  }

  val logo =
    Seq(
      """   ________                           _     __""",
      """  / ____/ /_  _________  ____  ____  (_)___/ /""",
      """ / /   / __ \/ ___/ __ \/ __ \/ __ \/ / __  /""",
      """/ /___/ / / / /  / /_/ / / / / /_/ / / /_/ /""",
      """\____/_/ /_/_/   \____/_/ /_/\____/_/\__,_/""")

  val syntax =
    Seq(
      "syntax: chronoid <filename>.<extension> <interval> <target> [<screeninfo>]",
      "where",
      "\t-e <?> | --extension <?>\t\tFile extention (and output format) of output file.",
      "\t-i <?> | --interval <?>\t\t\tInterval between two screenshots in milliseconds.",
      "\t-f <?> | --filename <?>\t\t\tSuffix of the name, which is used for screenshots.",
      "\t-t <?> | --target <?>\t\t\tTarget directory",
      "\t-si | --screeninfo\t\tPrint the available screen devices.",
      "\t-s <?> | --screen <?>\t\t\tTake screenshots of screen described by descriptor, valid descriptors: all, default and device ids (see --screeninfo)",
      "",
      "\tOptions can be given in any order.")

  def help(withLogo: Boolean) = {
    val prefix = if (withLogo) logo :+ "" else Seq.empty[String]
    (prefix ++ syntax).mkString("\n")
  }

  private def takeScreenShotOf(bounds: Rectangle): BufferedImage = {
    new Robot().createScreenCapture(bounds)
  }

  private def takeScreenShot(screenInfo: ScreenInfo): BufferedImage =
    screenInfo match {
      case ScreenInfo.All =>
        val screenSize = Toolkit.getDefaultToolkit.getScreenSize
        val image = takeScreenShotOf(new Rectangle(screenSize))
        image
      case ScreenInfo.Default =>
        val env   = GraphicsEnvironment.getLocalGraphicsEnvironment
        val gconf = env.getDefaultScreenDevice.getDefaultConfiguration
        takeScreenShotOf(gconf.getBounds)
      case ScreenInfo.Single(index) =>
        val env = GraphicsEnvironment.getLocalGraphicsEnvironment
        val dev = env.getScreenDevices.apply(index)
        val gconf = dev.getDefaultConfiguration
        takeScreenShotOf(gconf.getBounds)
    }

  private def execute(settings: Settings): Unit = {
    val Settings(Some(ext), Some(inv), Some(name), Some(dst), Some(screenInfo)) = settings
    @volatile var running = true

    def filename(n: Int): String =
      s"$name-$n.${ext.fileExtentsion}"

    @tailrec
    def go(cnt: Int): Unit =
      if (running) {
        val fname = filename(cnt)
        val image = takeScreenShot(screenInfo)
        val file  = new File(dst, fname)

        ImageIO.write(image, ext.fileExtentsion, file)
        println(s"Created file '${file.getAbsolutePath}'!")
        Thread.sleep(inv)
        go(cnt + 1)
      } else {
        val nameTemplate = "\"" + s"$name-${"%d"}.${ext.fileExtentsion}" + "\""
        println("Screenshots can be combined to a timelapse with e.g.")
        println(s"ffmpeg -start_number 0 -r 10 -i $nameTemplate -q:v 1 -b:v 1500k $name.mp4")
        println()
      }

    SigHandler.onCtrlC { running = false }

    logo.foreach(println)
    println()
    go(0)
  }

  final def validateArgs(settings: Settings): Err[Settings] = {
    val Settings(ext, inv, fname, dst, sInfo) = settings

    type OutF = OutputFormat
    lazy val invErr:   Err[Int]    = Left("No interval was specified!")
    lazy val dstErr:   Err[File]   = Left("No output directory was specified!")
    lazy val extErr:   Err[OutF]   = Left("No file extension was specified!")
    lazy val fnameErr: Err[String] = Left("No filename was specified!")
    lazy val screenInfo = sInfo getOrElse ScreenInfo.All

    for {
      _ <- inv.fold(invErr)(Right(_)).right
      _ <- dst.fold(dstErr)(Right(_)).right
      _ <- fname.fold(fnameErr)(Right(_)).right
      _ <- ext.fold(extErr)(Right(_)).right
    } yield Settings(ext, inv, fname, dst, Option(screenInfo))
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
      case PrintScreens(_)        =>
        ScreenInfo.printScreenDevices()
        sys.exit(0)
      case Screen(sx)             => sx.right flatMap {
        case (info, rest)         => parseArgs(rest, settings.copy(screenInfo = Some(info)))
      }
      case ("-h" | "--help") :: _ => Left(logo.mkString("\n"))
      case hd :: _                => Left(s"Illegal argument '$hd'!")
      case _                      => Right(settings)
    }

}
