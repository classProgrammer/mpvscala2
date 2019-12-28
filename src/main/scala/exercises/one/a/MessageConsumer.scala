package exercises.one.a

import akka.actor.Actor
import exercises.one.util.PCUtil

class MessageConsumer extends Actor {
  import MessageProducer.Message

  override def receive: Receive = {
    case Message(id, text) =>
      println(s"   === MessageConsumer.received message: $text at ${PCUtil.now()}")
  }

  override def unhandled(message: Any): Unit = {
    println(s"   === UNHANDLED: ${self.path.name}: '$message'")
  }
}
