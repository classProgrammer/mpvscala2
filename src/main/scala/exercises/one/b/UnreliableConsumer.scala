package exercises.one.b

import java.text.SimpleDateFormat
import java.util.Calendar

import akka.actor.Actor
import exercises.one.util.PCUtil


class UnreliableConsumer(successProbability: Float) extends Actor {
  import exercises.one.b.AtLeastOnceProducer._
  import exercises.one.a.MessageProducer._

  val rand = scala.util.Random

  override def receive: Receive = {
    case Message(id, text) =>
      if (rand.nextFloat() > successProbability) {
        println(s"   === UnreliableConsumer PROCESSED: '$text' @ ${PCUtil.now()}")
      }
      else {
        println(s"   === UnreliableConsumer PROCESSED: '$text' @ ${PCUtil.now()}")
        context.sender() ! Confirm(id, text)
      }
  }

  override def unhandled(message: Any): Unit = {
    println(s"   === UNHANDLED: ${self.path.name}: '$message'")
  }
}
