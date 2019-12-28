package exercises.one.b

import akka.actor.{Actor, ActorRef, Props}
import exercises.one.b.AtLeastOnceProducer.{AllMessagesConfirmed}
import exercises.one.a.MessageProducer._

class MainActorExactlyOnce extends Actor {
  val producer: ActorRef = context.actorOf(Props(new AtLeastOnceProducer(500)), "MessageProducer")
  val consumer: ActorRef = context.actorOf(Props(new UnreliableConsumerProcessOnce(0.7f)), "MessageConsumer")

  producer ! Produce("h1", consumer)
  producer ! Produce("h0w w45 y0ur d4y", consumer)
  producer ! Produce("n1c3 2 h34r", consumer)
  producer ! Produce("m1n3 w45 0k", consumer)
  producer ! Produce("by3", consumer)

  context watch producer



  override def receive: Receive = {
    case AllMessagesConfirmed() =>
        println("TERMINATING: All messages confirmed")
        context.system.terminate()
  }
}
