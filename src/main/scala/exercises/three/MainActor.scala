package exercises.three

import akka.actor.{Actor, ActorRef, PoisonPill, Props, Terminated}
import exercises.three.storage.{RoundRobinStorage, StorageActor}
import exercises.three.util.TemperatureUnit
import exercises.three.weatherstation.WeatherStationActor.{QueueEmpty, Start, Stop}
import exercises.three.weatherstation.WeatherStationActor

class MainActor extends Actor {
  val runtimeMs: Long = 5000
  val consumers = Seq(
    context.actorOf(Props(new StorageActor("fs1", "weather_station_1.txt", 5, 2000, true, 200)), "Consumer_1"),
    context.actorOf(Props(new StorageActor("fs2", "weather_station_2.txt", 7, 2000, true, 200)), "Consumer_2")
  )

  val roundRobin: ActorRef = context.actorOf(Props(new RoundRobinStorage(consumers)))

  val producers = Seq(
    context.actorOf(Props(new WeatherStationActor("Linz", roundRobin, 16.3f, 21.4f, 250, TemperatureUnit.Celsius)), "Producer_Linz"),
    context.actorOf(Props(new WeatherStationActor("Vienna", roundRobin, 18.93f, 24.56f, 300, TemperatureUnit.Celsius)), "Producer_Vienna"),
    context.actorOf(Props(new WeatherStationActor("Dornbirn", roundRobin, 14.78f, 18.77f, 175, TemperatureUnit.Celsius)), "Producer_Dornbirn"),
    context.actorOf(Props(new WeatherStationActor("Las_Vegas", roundRobin, 55.66f, 80.44f, 325, TemperatureUnit.Fahrenheit)), "Producer_Vegas")
  )
  producers.foreach(prod => prod ! Start())

  var noProducers = producers.size

  context.children.foreach(context.watch(_))
  Thread.sleep(runtimeMs) // let system run for ~5 seconds
  producers.foreach(prod => prod ! Stop())

  override def receive: Receive = {
    case Terminated(actor) =>
      if (producers.contains(actor)) {
        noProducers -= 1
      }
      if (noProducers == 0) {
        noProducers = -1
        consumers.foreach(consumer => consumer ! Stop())
        roundRobin ! PoisonPill
      }
      else if (context.children.size == 0) {
        context.system.terminate()
      }
  }
}
