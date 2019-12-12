package mpv.basics.actors.simple

import akka.actor.{Actor, ActorRef, PoisonPill, Props, Terminated}



class SimpleMainActor extends Actor {
  import mpv.basics.actors.simple.SimplePrimeCalculator.{Find, Found}
  //println("   +++ SimpleMainActor created")
  val calc: ActorRef = context.actorOf(Props[SimplePrimeCalculator], "SimplePrimeCalculator")

  // !(ActorRef, Any)
  calc ! Find(2, 100)
  calc ! Find(1000, 1200)
  calc ! Find(10000, 10400)

  calc ! PoisonPill

  context watch calc

  override def receive: Receive = {
    case Found(l, u, primes) =>
      println(s"   === SimpleMainActor.received: Primes from $l to $u = [${primes.mkString(", ")}]")
    case Terminated(actor) =>
      context.system.terminate()
  }

  override def unhandled(message: Any): Unit = {
    println(s"   === UNHANDLED: ${self.path.name}: '$message'")
  }


}
