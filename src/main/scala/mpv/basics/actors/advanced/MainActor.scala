package mpv.basics.actors.advanced

import akka.actor.{Actor, Props, Terminated}

class MainActor extends Actor {
  import PrimeCalculator.{Find, Found, Failed}

  //context.actorOf(Props(classOf[PrimeCalculator], 1, 100))
  context.actorOf(Props(classOf[PrimeCalculator], 1000, 1200))

  context.children.foreach(context.watch(_))


  override def receive: Receive = {
    case Found(l, u, primes) =>
      println(s"   === MainActor.received: Primes from $l to $u = [${primes.mkString(", ")}]")
    case Terminated(actor) =>
      if (context.children.size == 0) {
        context.system.terminate()
      }
    case Failed(l, u) => {
      println(s"   === FAILED MainActor: computation of primes from $l to $u failed")
    }
  }

  override def unhandled(message: Any): Unit = {
    println(s"   === UNHANDLED: ${self.path.name}: '$message'")
  }
}
