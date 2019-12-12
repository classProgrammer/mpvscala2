package mpv.basics.actors.advanced

import akka.actor.Actor
import akka.pattern.pipe
import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class PrimeFinder(lower: Int, upper: Int) extends Actor {
  import PrimeCalculator._

  Future { (lower to upper) filter isPrime } pipeTo self

  override def receive: Receive = {
    case primes: Seq[_] =>
      println(s"      === ${self.path.name} SUCCEEDED(${Thread.currentThread().getId}): primes=${primes.mkString(", ")}")
      // context.parent => otherwise, through pipe message is sent to self from self, so sender is self
      context.parent ! Found(lower, upper, primes.asInstanceOf[Seq[Int]])
      context.stop(self)
  }

  override def unhandled(message: Any): Unit = {
    println(s"      === UNHANDLED: ${self.path.name}: '$message'")
  }
}
