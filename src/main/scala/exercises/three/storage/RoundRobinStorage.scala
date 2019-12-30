package exercises.three.storage

import akka.actor.{Actor, ActorRef, Terminated}
import akka.routing.{ActorRefRoutee, RoundRobinRoutingLogic, Router}
import exercises.three.weatherstation.WeatherStationActor.Message

class RoundRobinStorage(var stores: Seq[ActorRef]) extends Actor {
  val ring = stores.map(store => {
    context.watch(store)
    ActorRefRoutee(store)
  }).toIndexedSeq

  var router = Router(RoundRobinRoutingLogic(), ring)

  def receive: Receive = {
    case msg: Message =>
      router.route(msg, sender())
    case Terminated(a) =>
      router = router.removeRoutee(a)
  }

  override def unhandled(message: Any): Unit = {
    println(s"   === UNHANDLED: ${self.path.name}: '$message'")
  }
}
