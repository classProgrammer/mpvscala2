package mpv.basics.actors.advanced

import akka.actor.{ActorSystem, Props, Terminated}
import scala.concurrent.{Await, Future}
import scala.concurrent.duration.Duration

object PrimeCalculatorApp extends App{
  println("========== PrimeCalculatorApp ==========")

  val system = ActorSystem("PrimeCalcSystem")

  system.actorOf(Props[MainActor], "MainActor")
}
