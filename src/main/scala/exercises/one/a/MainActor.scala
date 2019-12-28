package exercises.one.a

import akka.actor.{Actor, ActorRef, PoisonPill, Props, Terminated}
import exercises.one.a.MessageProducer.{Produce, ProduceDelay}


class MainActor extends Actor {
  val producer: ActorRef = context.actorOf(Props[MessageProducer], "MessageProducer")
  val consumer: ActorRef = context.actorOf(Props[MessageConsumer], "MessageConsumer")

  producer ! Produce("hi", consumer)
  producer ! Produce("sup", consumer)
  producer ! Produce("bye", consumer)

  producer ! ProduceDelay("hi delayed", consumer, 1000)
  producer ! ProduceDelay("sup delayed", consumer, 1000)
  producer ! ProduceDelay("bye delayed", consumer, 1000)

  producer ! PoisonPill
  context watch producer

  override def receive: Receive = {
    case Terminated(actor) =>
      if (actor.compareTo(producer) == 0) {
        consumer ! PoisonPill
        context watch consumer
      }
      else {
        context.system.terminate()
      }
  }
}
