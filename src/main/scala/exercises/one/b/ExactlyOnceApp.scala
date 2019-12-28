package exercises.one.b

import akka.actor.{ActorSystem, Props}

object ExactlyOnceApp extends App {
  println("=========== ExactlyOnceApp ===========")

  val system = ActorSystem("ExactlyOnceApp")
  system.actorOf(Props[MainActorExactlyOnce], "MainActorExactlyOnce")

}
