package mpv.basics.reactivestreams

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.{Done, NotUsed}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object ReactiveStreams extends App {

  implicit val system: ActorSystem = ActorSystem("MySystem")

  def isPrime(n: Long): Boolean = {
    val upper = math.sqrt(n).toInt + 1
    (2 until upper) forall (i => n % i != 0)
  }

  def trace[T](expr: => T): T = {
    val result = expr
    println(s"$result (thread=${Thread.currentThread.getId})")
    result
  }

  def tracef[T](expr: => T, formatString: String = "%s"): T = {
    val result = expr
    println((formatString + " (thread=%d)")
      .format(result, Thread.currentThread.getId))
    result
  }

  def computePrimes(): Unit = {
    
  }

  def computePrimesAsync(): Unit = {

  }

  println("----------------- computePrimes -----------------")
  computePrimes()

  println("-------------- computePrimesAsync ---------------")
  computePrimesAsync()

  system.terminate()
}
