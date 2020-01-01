package exercises.two.ask

import akka.actor.{Actor, ActorRef, Props, ReceiveTimeout}
import akka.util.Timeout
import scala.concurrent.{Promise, TimeoutException}

class AskActor(message: Any, timeout: Timeout, sender: ActorRef, target: ActorRef, promise: Promise[Any]) extends Actor {
  private val name = self.path.name
  private def println(msg: Any): Unit = Console.println(s"$name => $msg")

  println("created")
  target ! message
  context.setReceiveTimeout(timeout.duration)
  println("sent message to target")

  override def receive: Receive = {
    case ReceiveTimeout =>
      println("Timeout Received")
      promise.failure(new TimeoutException(s"$name => Timeout received"))
      context.system.terminate()

    case message =>
      println(s"received '$message'")
      promise.success(message)
      context.system.terminate()
  }
}
