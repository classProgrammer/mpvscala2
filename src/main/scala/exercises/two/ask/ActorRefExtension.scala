package exercises.two.ask

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.util.Timeout

import scala.concurrent.{Future, Promise}

class ActorRefExtension(private val actorRef: ActorRef) {
  private val system = ActorSystem("MattersNot")

  def ?(message: Any)
       (implicit timeout: Timeout, sender: ActorRef = Actor.noSender): Future[Any] = {
    val promise = Promise[Any]()
    val target = actorRef
    system.actorOf(Props(new AskActor(message, timeout, sender, target, promise)), "MyAskActor")
    promise.future
  }
}
