package exercises.one.a

import akka.actor.{ActorSystem, Props}

object SimpleMessageExchange extends App {
  println("=========== SimpleMessageExchange App ===========")

  val system = ActorSystem("SimpleMessageExchange")
  system.actorOf(Props[MainActor], "MainActor")
}
