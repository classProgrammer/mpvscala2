package exercises.one.d

import akka.actor.{Actor, ActorRef, Props}
import exercises.one.a.MessageProducer._
import exercises.one.b.AtLeastOnceProducer._
import exercises.one.c.UnreliableConsumerProcessOnce


class MainActorExactlyOnceRetryLimit extends Actor {
  val producer: ActorRef = context.actorOf(Props(new AtLeastOnceProducerRetryLimit(500, 2)), "MessageProducer")
  val consumer: ActorRef = context.actorOf(Props(new UnreliableConsumerProcessOnce(0.05f)), "MessageConsumer")

  producer ! Produce("h1", consumer)
  producer ! Produce("h0w w45 y0ur d4y", consumer)
  producer ! Produce("n1c3 2 h34r", consumer)
  producer ! Produce("m1n3 w45 0k", consumer)
  producer ! Produce("by3", consumer)

  context watch producer

  override def receive: Receive = {
    case AllMessagesConfirmed() =>
        println("TERMINATING: All messages processed or discarded")
        context.system.terminate()
  }
}
