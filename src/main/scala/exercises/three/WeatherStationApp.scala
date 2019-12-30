package exercises.three

import akka.actor.{ActorSystem, Props}

object WeatherStationApp extends App {
  println("=========== WeatherStationApp ===========")

  val system = ActorSystem("WeatherStationApp")
  system.actorOf(Props[MainActor], "MainActor")
}
