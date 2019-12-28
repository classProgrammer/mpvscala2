package exercises.one.b

import java.util.UUID

import akka.actor.Actor
import exercises.one.util.PCUtil


class UnreliableConsumerProcessOnce(successProbability: Float) extends Actor {
  import exercises.one.b.AtLeastOnceProducer._
  import exercises.one.a.MessageProducer._

  val rand = scala.util.Random
  var processed = Map.empty[UUID, UUID]


  override def receive: Receive = {
    case Message(id, text) =>

      if(!processed.contains(id)) {
        println(s"   === UnreliableConsumer PROCESSED: '$text' @ ${PCUtil.now()}")
        processed += Tuple2(id, id)
      }

      if (!(rand.nextFloat() > successProbability)) {
        context.sender() ! Confirm(id, text)
      }
  }

  override def unhandled(message: Any): Unit = {
    println(s"   === UNHANDLED: ${self.path.name}: '$message'")
  }
}
