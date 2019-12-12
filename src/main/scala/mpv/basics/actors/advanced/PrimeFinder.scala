package mpv.basics.actors.advanced

import akka.actor.Actor
import akka.pattern.pipe

import scala.concurrent.Future
import scala.util.Random

class PrimeFinder(lower: Int, upper: Int) extends Actor {
  import PrimeCalculator._
  import context.dispatcher
  Future { (lower to upper) filter isPrime } pipeTo self

  private def failSometimes(probability: Double) {
    if (Random.nextDouble() <= probability) {
      println(s"      FAILED: ${self.path.name}")
      throw new ArithmeticException("Exception in PrimeFinder")
    }
  }

  override def postRestart(reason: Throwable): Unit = {
    println(s"      RESTARTED: ${self.path.name}")
    super.postRestart(reason)
  }



  override def receive: Receive = {
    case primes: Seq[_] =>
      failSometimes(0.4)
      println(s"      === ${self.path.name} SUCCEEDED(${Thread.currentThread().getId}): primes=${primes.mkString(", ")}")
      // context.parent => otherwise, through pipe message is sent to self from self, so sender is self
      context.parent ! Found(lower, upper, primes.asInstanceOf[Seq[Int]])
      context.stop(self)
  }

  override def unhandled(message: Any): Unit = {
    println(s"      === UNHANDLED: ${self.path.name}: '$message'")
  }
}
