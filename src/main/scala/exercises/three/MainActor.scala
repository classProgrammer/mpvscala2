package exercises.three

import akka.actor.{Actor, ActorRef, PoisonPill, Props, Terminated}
import exercises.three.storage.{RoundRobinStorage, StorageActor}
import exercises.three.util.TemperatureUnit
import exercises.three.weatherstation.WeatherStationActor.{QueueEmpty, Start, Stop}
import exercises.three.weatherstation.WeatherStationActor


class MainActor extends Actor {
  val consumer1: ActorRef = context.actorOf(Props(new StorageActor("fs1", "weather_station_1.txt", 15, 2000, true, 200)), "Consumer_1")
  val consumer2: ActorRef = context.actorOf(Props(new StorageActor("fs2", "weather_station_2.txt", 15, 2000, true, 200)), "Consumer_2")
  val consumers = Seq(consumer1, consumer2)

  val roundRobin: ActorRef = context.actorOf(Props(new RoundRobinStorage(consumers)))

  val producer1: ActorRef = context.actorOf(Props(new WeatherStationActor("Linz", roundRobin, 16.3f, 21.4f, 250, TemperatureUnit.Celsius, false, 200)), "Producer_Linz")
  val producer2: ActorRef = context.actorOf(Props(new WeatherStationActor("Vienna", roundRobin, 18.93f, 24.56f, 300, TemperatureUnit.Celsius, false, 200)), "Producer_Vienna")
  val producer3: ActorRef = context.actorOf(Props(new WeatherStationActor("Dornbirn", roundRobin, 14.78f, 18.77f, 175, TemperatureUnit.Celsius, false, 200)), "Producer_Dornbirn")
  val producer4: ActorRef = context.actorOf(Props(new WeatherStationActor("Las_Vegas", roundRobin, 55.66f, 80.44f, 325, TemperatureUnit.Fahrenheit, false, 200)), "Producer_Vegas")

  val producers = Seq(producer1, producer2, producer3, producer4)
  producers.foreach(prod => prod ! Start())

  var noProducers = producers.size

  context.children.foreach(context.watch(_))

  Thread.sleep(5000)

  producers.foreach(prod => prod ! Stop())

  override def receive: Receive = {
    case Terminated(actor) =>
      noProducers -= 1
      if (noProducers == 0) {
        consumers.foreach(consumer => consumer ! Stop())
        roundRobin ! PoisonPill
      }
      else if (context.children.size == 0) {
        context.system.terminate()
      }
  }
}
