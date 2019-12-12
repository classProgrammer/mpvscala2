package mpv.basics.actors.advanced

import akka.actor.SupervisorStrategy.Restart
import akka.actor.{Actor, ActorRef, OneForOneStrategy, Props, SupervisorStrategy, Terminated}
import mpv.basics.actors.advanced.PrimeCalculator.Failed

object PrimeCalculator {
  case class Find(lower: Int, upper: Int)
  case class Found(lower: Int, upper: Int, primes: Seq[Int])
  case class Failed(lower: Int, upper: Int)

  def isPrime(number: Int): Boolean = {
    (2 to math.sqrt(number).toInt) forall(n => number % n != 0)
  }
}

class PrimeCalculator(lower: Int, upper: Int) extends Actor {
  import RangeUtil.splitIntoIntervals
  import PrimeCalculator.Found

  val MAX_WORKERS = Runtime.getRuntime.availableProcessors()
  val primes = scala.collection.mutable.SortedSet.empty[Int]
  val completedWorkers = scala.collection.mutable.Set.empty[ActorRef]
  var nWorkers = 0

  createChildren()
  registerDeathwatch()

  private def registerDeathwatch() = {
    context.children foreach context.watch
  }

  private def createChildren(): Unit = {
    for ((l, u) <- splitIntoIntervals(lower, upper, MAX_WORKERS)) {
      context.actorOf(Props(classOf[PrimeFinder], l, u),
        s"PrimeFinder_${l}_$u")
    }
    nWorkers = context.children.size
  }

  override val supervisorStrategy = OneForOneStrategy(2) {
    case _: ArithmeticException => Restart
    case ex => SupervisorStrategy.defaultStrategy.decider(ex)
  }

  override def receive: Receive = {
    case Found(l, u, p) =>
      primes ++= p
      completedWorkers += sender()

      if (completedWorkers.size >= nWorkers) {
        context.parent ! Found(lower, upper, primes.toSeq)
        context.stop(self)
      }
    case Terminated(actor) =>
      if (! completedWorkers.contains(actor)) {
        context.parent ! Failed(lower, upper)
        context.stop(self)
      }
  }

  override def unhandled(message: Any): Unit = {
    println(s"   === UNHANDLED: ${self.path.name}: '$message'")
  }
}
