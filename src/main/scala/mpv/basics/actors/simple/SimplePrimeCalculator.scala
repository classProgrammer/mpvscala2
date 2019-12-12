package mpv.basics.actors.simple

import akka.actor.Actor

// companion
object SimplePrimeCalculator {
  case class Find(lower: Int, upper: Int)
  case class Found(lower: Int, upper: Int, primes: Seq[Int])

  def isPrime(number: Int): Boolean = {
    (2 to math.sqrt(number).toInt) forall(n => number % n != 0)
  }
}

class SimplePrimeCalculator extends Actor {
  import SimplePrimeCalculator._
  override def receive: Receive = {
    case Find(l, u) =>
      val primes: Seq[Int] = (l to u) filter isPrime
      sender() ! Found(l, u, primes)
  }

  override def unhandled(message: Any): Unit = {
    println(s"   === UNHANDLED: ${self.path.name}: '$message'")
  }
}
