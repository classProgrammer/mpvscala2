package exercises.one.d

import akka.actor.{ActorSystem, Props}

object ExactlyOnceAppRetryLimit extends App {
  println("=========== ExactlyOnceAppRetryLimit ===========")

  val system = ActorSystem("ExactlyOnceAppRetryLimit")
  system.actorOf(Props[MainActorExactlyOnceRetryLimit], "MainActorExactlyOnceRetryLimit")
}
