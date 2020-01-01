package exercises.three.weatherstation
import exercises.three.util.{MeasurementGenerator, TemperatureUnit}
import akka.actor.{Actor, ActorRef, Cancellable}
import java.time.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import exercises.three.util.TemperatureUnit.TemperatureUnit

import scala.concurrent.Future

object WeatherStationActor {
  case class Message(weatherStationName: String, measurement: (String, Float, TemperatureUnit))
  case class Start()
  case class Stop()
  case class QueueEmpty(actor: ActorRef)
}

class WeatherStationActor(val name: String, val consumer: ActorRef, val lower: Float = -40.0f, val upper: Float = 40.0f, val messageIntervalMs: Int = 250, val unit: TemperatureUnit = TemperatureUnit.Celsius) extends Actor {
  import WeatherStationActor.{Start, Stop, Message}

  private var job: Cancellable = null
  private val generator: MeasurementGenerator = new MeasurementGenerator(lower, upper, unit)
  private val messageDelay: Duration = Duration.ofMillis(messageIntervalMs)
  private var noMessages = 0

  private def println(message: Any): Unit = {
    printAsync(s"$message (thread id=${Thread.currentThread.getId})")
  }
  private def printAsync(msg: Any): Unit = {
    Future { Console.println(s"$msg") }
  }

  private def start() = {
    if (job == null) {
      println(s"   ### WeatherStation '$name' started")
      job = context.system.scheduler.scheduleWithFixedDelay(
        messageDelay,
        messageDelay,
        () => {
          val measurement = generator.getMeasurement()
          noMessages += 1
          println(s"$name no.$noMessages => produced '$measurement'")
          consumer ! Message(name, measurement)

        },
        context.system.dispatcher)
    }
  }

  private def stop() = {
    if (job != null) {
      job.cancel()
      println(s"   ### WeatherStation '$name' stopped, $noMessages messages generated")
      context.stop(self)
    }
  }

  override def receive: Receive = {
    case Start() => start()
    case Stop()  => stop()
  }

  override def unhandled(message: Any): Unit = {
    println(s"   === UNHANDLED: ${self.path.name}: '$message'")
  }
}
