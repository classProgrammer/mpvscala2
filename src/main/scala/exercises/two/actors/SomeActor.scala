package exercises.two.actors

import akka.actor.Actor

import scala.concurrent.duration._

object SomeActor {
  case class Message()
}

class SomeActor(delay: FiniteDuration = 500.millis) extends Actor {
  import SomeActor.Message
  private val name = self.path.name
  private def println(msg: Any): Unit = Console.println(s"$name => $msg")

  override def receive: Receive = {
    case Message() =>
      println("start work")
      Thread.sleep(delay.toMillis)
      println("finished work")
      sender ! Message()
  }

  override def unhandled(message: Any): Unit = {
    println(s"   === UNHANDLED: ${self.path.name}: '$message'")
  }
}
