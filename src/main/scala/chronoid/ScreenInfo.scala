package chronoid

import java.awt.GraphicsEnvironment

sealed trait ScreenInfo

object ScreenInfo {

  case object All extends ScreenInfo
  case object Default extends ScreenInfo
  case class Single(screenIndex: Int) extends ScreenInfo

  def printScreenDevices(): Unit = {
    val env = GraphicsEnvironment.getLocalGraphicsEnvironment
    for (dev <- env.getScreenDevices) {
      val dmode = dev.getDisplayMode; import dmode._
      val refreshRate = if (getRefreshRate != 0) s" ${getRefreshRate}Hz" else ""
      val deviceId = s"screen${dev.getIDstring}"
      println(s"$deviceId (${getWidth}x$getHeight$refreshRate)")
    }
  }

  def screenDeviceIndexById(id: String): Option[Int] = {
    val env = GraphicsEnvironment.getLocalGraphicsEnvironment
    val index = env.getScreenDevices.indexWhere { dev =>
      val deviceId = s"screen${dev.getIDstring}"
      deviceId == id
    }

    if (index >= 0) Option(index)
    else Option.empty[Int]
  }

}
