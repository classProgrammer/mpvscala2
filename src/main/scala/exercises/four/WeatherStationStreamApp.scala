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

import scala.concurrent.{Await, Future}
import scala.concurrent.duration._

object WeatherStationTypes {
  type Measurement = (String, (String, Float, TemperatureUnit))
  type SourceType = Source[Measurement, Cancellable]
  type FlowType = Flow[Measurement, ByteString, NotUsed]
  type SinkType = Sink[ByteString, Future[IOResult]]
}

object WeatherStationConfig {
  val fileName = "streamed_weather_station.txt"
  val file = Paths.get(s"./src/main/scala/exercises/four/file/$fileName")
  val batchSize = 7
  val elements = 100
  val elementsToThrottle = 1
  val parallelism = 4
  val generators = Seq(
    new MeasurementGenerator(20.2f, 22.15f, TemperatureUnit.Celsius),
    new MeasurementGenerator(22.55f, 24.12f, TemperatureUnit.Celsius),
    new MeasurementGenerator(18.45f, 19.68f, TemperatureUnit.Celsius),
    new MeasurementGenerator(40.77f, 52.93f, TemperatureUnit.Fahrenheit)
  )
}

object WeatherStationStreamApp extends App {
  import WeatherStationTypes._
  import WeatherStationConfig._
  import system.dispatcher

  private implicit val system: ActorSystem = ActorSystem("WeatherStation")

  private def println(message: Any): Unit = {
    printAsync(s"$message (thread id=${Thread.currentThread.getId})")
  }
  private def printAsync(msg: Any): Unit = {
    Future { Console.println(s"$msg") }
  }

  private def getFlow(): FlowType = {
    Flow[Measurement].map(x => {
      println(s"+++ write element")
      measurementAsByteStringLine(x)
    })
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
    Flow[Measurement].throttle(elementsToThrottle, delay).map(x => {
      println(s"+++ write element")
      measurementAsByteStringLine(x)
    })
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

  def getChunkedFlowMapAsync(batchSize: Int): FlowType = {
    Flow[Measurement].batch(batchSize, Seq(_)) (makeCollection)
      .mapAsync(parallelism)(collection => Future {
        println(s"--- Write ${collection.size} items to file")
        ByteString(collection.map(measurementAsStringLine).mkString("\n"))
    })
  }

  def getChunkedFlowMapAsync(batchSize: Int, delay: FiniteDuration): FlowType = {
    Flow[Measurement].batch(batchSize, Seq(_)) (makeCollection)
      .throttle(elementsToThrottle, delay)
      .mapAsync(parallelism)(collection => Future {
        println(s"--- Write ${collection.size} items to file")
        ByteString(collection.map(measurementAsStringLine).mkString(""))
    })
  }

  def getChunkedFlowMapAsync(delay: FiniteDuration): FlowType = {
    Flow[Measurement]
      .throttle(elementsToThrottle, delay)
      .mapAsync(parallelism)(elem => Future {
        println(s"--- write to file")
        measurementAsByteStringLine(elem)
      })
  }

  def getChunkedFlow(batchSize: Int, delay: FiniteDuration): FlowType = {
    Flow[Measurement]
      .throttle(elementsToThrottle, delay)
      .map(elem => {
        println(s"--- write to file")
        measurementAsByteStringLine(elem)
      })
  }

  def main(): Unit = {
    println("========== WeatherStationStream App ==========")

    val sources = Seq(
      getSource("Linz", generators(0), 2.millis),
      getSource("Vienna", generators(1), 3.millis),
      getSource("Dornbirn", generators(2), 5.millis),
      getSource("Las Vegas", generators(3), 7.millis)
    )

    val flow = getChunkedFlowMapAsync(batchSize, 200.millis)
    val sink = getSink(file)

    val futures: Future[Seq[IOResult]] = Future.sequence(
      sources.map(source => pipeline(source, flow, sink, elements)))

    Await.ready(futures, Duration.Inf)
    println("========== END WeatherStationStream App ==========")
    Thread.sleep(1000)
    system.terminate()
  }

  private def getSource(name: String, gen: MeasurementGenerator, interval: FiniteDuration): SourceType = {
    var number = 1
    Source.tick(interval, interval, 1).map(_ => {
      val m = gen.getMeasurement()
      println(s"$name:$number => $m")
      number += 1
      (name, m)
    })
  }

  main()
}
