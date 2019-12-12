package mpv.basics.actors.simple

import akka.actor.{ActorSystem, Props, Terminated}

import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}

object SimplePrimeCalculatorApp extends App{
  println("=========== SimplePrimeCalculatorApp ===========")

  val system = ActorSystem("PrimeCalcSystem")

  system.actorOf(Props[SimpleMainActor], "SimpleMainActor")

  //Thread.sleep(1000)
  //val a: Future[Terminated] = system.terminate()

  //Await.ready(a, Duration.Inf)
}
