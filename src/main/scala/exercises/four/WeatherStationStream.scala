package exercises.four

import java.nio.file.{Path, Paths}
import java.nio.file.StandardOpenOption.{APPEND, WRITE}

import akka.NotUsed
import akka.actor.{ActorSystem, Cancellable}
import akka.stream.IOResult
import akka.util.ByteString
import exercises.three.util.{MeasurementGenerator, TemperatureUnit}
import exercises.three.util.TemperatureUnit.TemperatureUnit
import akka.stream.scaladsl.{FileIO, Flow, Sink, Source}
import exercises.four.WeatherStationStream.pipeline

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.duration.Duration

class WeatherStation {

}

object WeatherStationStream extends App {
  private implicit val system: ActorSystem = ActorSystem("WeatherStation")

  import system.dispatcher
  private type Measurement = (String, (String, Float, TemperatureUnit))
  private type SourceType = Source[Measurement, Cancellable]
  private type FlowType = Flow[Measurement, ByteString, NotUsed]
  private type SinkType = Sink[ByteString, Future[IOResult]]
  private val generator = new MeasurementGenerator(20.2f, 22.15f, TemperatureUnit.Celsius)
  private val generateInterval = 1.millis
  private val fileName = "streamed_weather_station.txt"
  private val file = Paths.get(s"C:\\Users\\GeraldSpenlingwimmer\\IdeaProjects\\mpvscala2\\src\\main\\scala\\exercises\\four\\file\\$fileName")
  private val chunkSize = 4
  private val elementsToThrottle = 4
  private val elements = 23


  private def println(msg: Any) = Console.println(s"$msg (thread id=${Thread.currentThread.getId})")

  private def getFlow(): FlowType = {
    Flow[Measurement].map(measurementAsByteStringLine)
  }

  private def measurementAsByteStringLine(measurement: Measurement): ByteString = {
    ByteString(s"${measurementAsString(measurement)}\n")
  }

  private def measurementAsString(measurement: Measurement) = {
    s"(${measurement._1}, ${measurement._2._2}, Degree ${measurement._2._3}, ${measurement._2._1})"
  }

  private def measurementAsStringLine(measurement: Measurement) = {
    s"${measurementAsString(measurement)}\n"
  }

  private def getSink(path: Path): SinkType = {
    FileIO.toPath(path, Set(WRITE, APPEND))
  }

  private def getFlow(delay: FiniteDuration): FlowType = {
    Flow[Measurement].throttle(1, delay).map(measurementAsByteStringLine)
  }

  private def pipeline(source: SourceType, flow: FlowType, sink: SinkType, elems: Int): Future[IOResult] = {
    source limit (elems - 1) via flow runWith sink
  }

  private def pipeline(source: SourceType, flow: FlowType, sink: SinkType): Future[IOResult] = {
    source via flow runWith sink
  }

  def makeCollection(collection: Seq[Measurement], elem: Measurement): Seq[Measurement] = {
    collection :+ elem
  }

  def getChunkedFlow(chunkSize: Int): FlowType = {
    var number = 0
    Flow[Measurement].batch(chunkSize, Seq(_)) (makeCollection)
      .mapAsync(chunkSize)(collection => Future {
        number += collection.size
        println(s"--- Write ${collection.size} items to file, wrote $number")
        ByteString(collection.map(measurementAsStringLine).mkString("\n"))
    })
  }

  def getChunkedFlow(chunkSize: Int, delay: FiniteDuration): FlowType = {
    var number = 0
    Flow[Measurement].batch(chunkSize, Seq(_)) (makeCollection)
      .throttle(elementsToThrottle, delay)
      .mapAsync(chunkSize)(collection => Future {
        number += collection.size
        println(s"--- Write ${collection.size} items to file, wrote $number")
        ByteString(collection.map(measurementAsStringLine).mkString(""))
    })
  }

  def main(): Unit = {
    println("========== WeatherStationStream App ==========")

    val source = getSource("Linz", generator)
    val flow = getChunkedFlow(chunkSize, 500.millis)

    val source2 = getSource("Vienna", generator)
    val source3 = getSource("Dornbirn", generator)
    val source4 = getSource("Las Vegas", generator)

    val sink = getSink(file)

    val futures: Future[List[IOResult]] = Future.sequence(List(
        pipeline(source4, flow, sink, elements)
    ))

    Await.ready(futures, Duration.Inf)
    println("========== END WeatherStationStream App ==========")
    Thread.sleep(2000)
    system.terminate()
  }

  private def getSource(name: String, gen: MeasurementGenerator): SourceType = {
    var number = 1
    Source.tick(generateInterval, generateInterval, 1).map(_ => {
      val m = gen.getMeasurement()
      println(s"$name:$number => $m")
      number += 1
      (name, m)
    })
  }

  main()
}
