package exercises.one.b

import akka.actor.{ActorSystem, Props}

object AtLeastOnceApp extends App {
  println("=========== AtLeastOnceApp ===========")

  val system = ActorSystem("AtLeastOnceApp")
  system.actorOf(Props[MainActor], "MainActor")
}
