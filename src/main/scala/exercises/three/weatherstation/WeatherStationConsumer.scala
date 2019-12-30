package exercises.three.weatherstation

import akka.actor.Actor
import exercises.three.weatherstation.WeatherStationActor.{Message}

class WeatherStationConsumer extends Actor {

  override def receive: Receive = {
    case Message(name, measurement) =>
      println(s"${name} has sent '${measurement}'")
  }

  override def unhandled(message: Any): Unit = {
    println(s"   === UNHANDLED: ${self.path.name}: '$message'")
  }

}
