package exercises.one.a

import java.util.UUID
import java.util.UUID.randomUUID

import akka.actor.{Actor, ActorRef}

object MessageProducer {
  case class Message(id: UUID, text: String)
  case class ProduceDelay(text: String, receiver: ActorRef, millis: Int)
  case class Produce(text: String, receiver: ActorRef)
}

class MessageProducer extends Actor {
  import MessageProducer._

  override def receive: Receive = {
    case Produce(text, receiver) =>
      receiver ! Message(randomUUID(), text)
    case ProduceDelay(text, receiver, millis) =>
      Thread.sleep(millis)
      receiver ! Message(randomUUID(), text)
  }

  override def unhandled(message: Any): Unit = {
    println(s"   === UNHANDLED: ${self.path.name}: '$message'")
  }
}
