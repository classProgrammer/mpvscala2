package exercises.one.d

import java.time.Duration
import java.util.UUID
import java.util.UUID.randomUUID

import akka.actor.{Actor, ActorRef, Cancellable}
import exercises.one.b.AtLeastOnceProducer

import scala.collection.mutable.Map

object AtLeastOnceProducerRetryLimit {
  case class RetryLimitReached(id: UUID, text: String)
}

class AtLeastOnceProducerRetryLimit(interval: Long, retries: Int) extends Actor {
  import AtLeastOnceProducer._
  import AtLeastOnceProducerRetryLimit._
  import exercises.one.a.MessageProducer._

  var messages = Map.empty[UUID, Cancellable]

  def produce(text: String, receiver: ActorRef) = {
    val id = randomUUID()
    var times = retries + 1
    messages += Tuple2(
      id,
      context.system.scheduler.scheduleWithFixedDelay(
        Duration.ZERO,
        Duration.ofMillis(interval),
        () => {
          if (times < 1) {
            self ! RetryLimitReached(id, text)
          }
          else {
            println(s"   +++ send message: '${text}'")
            receiver ! Message(id, text)
            times -= 1
          }
        },
        context.system.dispatcher
      )
    )
  }

  override def receive: Receive = {
    case Produce(text, receiver) =>
      produce(text, receiver)

    case ProduceDelay(text, receiver, millis) =>
      Thread.sleep(millis)
      produce(text, receiver)

    case Confirm(id, text) =>
      println(s"   *** message: '${text}' : CONFIRMED")
      messages.get(id).foreach(c => {
        c.cancel()
      })
      messages -= id
      if (messages.size == 0) {
        context.parent ! AllMessagesConfirmed()
      }
    case RetryLimitReached(id, text) =>
      println(s"   --- RETRY LIMIT REACHED: '${text}' : DISCARDED")
      messages.get(id).foreach(c => {
        c.cancel()
      })
      messages -= id
      if (messages.size == 0) {
        context.parent ! AllMessagesConfirmed()
      }
  }

  override def unhandled(message: Any): Unit = {
    println(s"   === UNHANDLED: ${self.path.name}: '$message'")
  }
}
