package chronoid

import sun.misc.{ Signal, SignalHandler }

class SigHandler private (task: () => Unit) extends SignalHandler {
  override def handle(signal: Signal): Unit = task()
}

object SigHandler {

  def onCtrlC(task: => Unit): Unit =
    Signal.handle(new Signal("INT"), new SigHandler(task _))

}
