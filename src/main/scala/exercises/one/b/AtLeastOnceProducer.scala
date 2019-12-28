package exercises.one.b

import java.time.Duration
import java.util.UUID
import java.util.UUID.randomUUID
import scala.collection.mutable.Map
import akka.actor.{Actor, ActorRef, Cancellable}

object AtLeastOnceProducer {
  case class Confirm(id: UUID, text: String)
  case class AllMessagesConfirmed()
}

class AtLeastOnceProducer(interval: Long) extends Actor {
  import AtLeastOnceProducer._
  import exercises.one.a.MessageProducer._

  var messages = Map.empty[UUID, Cancellable]

  def produce(text: String, receiver: ActorRef) = {
    val id = randomUUID()
    messages += Tuple2(
      id,
      context.system.scheduler.scheduleWithFixedDelay(
        Duration.ZERO,
        Duration.ofMillis(interval),
        () => {
          println(s"   +++ send message: '${text}'")
          receiver ! Message(id, text)
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
  }

  override def unhandled(message: Any): Unit = {
    println(s"   === UNHANDLED: ${self.path.name}: '$message'")
  }
}
