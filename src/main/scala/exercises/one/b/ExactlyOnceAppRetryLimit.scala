package exercises.one.b

import akka.actor.{ActorSystem, Props}

object ExactlyOnceAppRetryLimit extends App {
  println("=========== ExactlyOnceAppRetryLimit ===========")

  val system = ActorSystem("ExactlyOnceAppRetryLimit")
  system.actorOf(Props[MainActorExactlyOnceRetryLimit], "MainActorExactlyOnceRetryLimit")

}
