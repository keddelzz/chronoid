
package object chronoid {

  type Args = List[String]

  type Err[T] = Either[String, T]

  type ErrX[T] = Err[(T, Args)]

  def error(lines: String*): Nothing = {
    lines.foreach(println)
    sys.exit(1)
  }

}
