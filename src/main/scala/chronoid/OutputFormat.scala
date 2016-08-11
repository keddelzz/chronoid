package chronoid

sealed abstract class OutputFormat(val fileExtentsion: String) {
  override def toString = s"*.$fileExtentsion"
}

object OutputFormat {

  case object Bmp  extends OutputFormat("bmp")
  case object Gif  extends OutputFormat("gif")
  case object Jpg  extends OutputFormat("jpg")
  case object Jpeg extends OutputFormat("jpeg")
  case object Png  extends OutputFormat("png")
  case object Wbmp extends OutputFormat("wbmp")

  private val lookup: Map[String, OutputFormat] =
    Seq(
      Bmp,
      Gif,
      Jpg,
      Jpeg,
      Png,
      Wbmp).map(x => (x.fileExtentsion, x)).toMap

  def apply(name: String): Option[OutputFormat] =
    lookup.get(name.toLowerCase())

}
