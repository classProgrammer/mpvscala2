package exercises.four

import java.nio.file.{Path, Paths}
import java.nio.file.StandardOpenOption.{APPEND, WRITE}

import akka.{Done, NotUsed}
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
  type FlowTypeSeq = Flow[Measurement, ByteString, NotUsed]
  type FlowType = Flow[Measurement, IOResult, NotUsed]
  type SinkType = Sink[ByteString, Future[IOResult]]
}

object WeatherStationConfig {
  val basePath = "./src/main/scala/exercises/four/file"
  val bulkWriteSize = 7
  val bulkWriteDelay = 100.millis
  val elementsToGenerate = 100
  val elementsToThrottle = bulkWriteSize
  val parallelism = 4
  val waitForGrouping: FiniteDuration = 200.millis
  val maxGenerationDelay = 10
  val minGenerationDelay = 1
  val diffGenerationDelay = maxGenerationDelay - minGenerationDelay
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

  // ======================== SOURCE ========================
  private def getSource(name: String, gen: MeasurementGenerator, interval: FiniteDuration): SourceType = {
    var number = 1
    Source.tick(interval, interval, 1).map(_ => {
      val measurement = gen.getMeasurement()
      println(s"$name:$number => $measurement")
      number += 1
      (name, measurement)
    })
  }

  // ======================== FLOWS ========================
  private def getFlow(): FlowTypeSeq = {
    Flow[Measurement].map(x => {
      println(s"+++ write element")
      measurementAsByteStringLine(x)
    })
  }

  private def getFlow(delay: FiniteDuration): FlowTypeSeq = {
    Flow[Measurement]
      .throttle(elementsToThrottle, delay)
      .map(x => {
        println(s"+++ write element")
        measurementAsByteStringLine(x)
    })
  }

  def getChunkedFlow(batchSize: Int, delay: FiniteDuration): FlowTypeSeq = {
    Flow[Measurement]
      .throttle(elementsToThrottle, delay)
      .groupedWithin(batchSize, waitForGrouping)
      .map(collection => {
        println(s"--- write ${collection.size} elements")
        measurementSequenceAsByteStringLine(collection)
      })
  }

  def getFlowMapAsync(delay: FiniteDuration, sink: SinkType): FlowType = {
    Flow[Measurement]
      .throttle(elementsToThrottle, delay)
      .mapAsync(parallelism)(elem => {
        println(s"--- write to file")
        Source(Seq(elem)).map(measurementAsByteStringLine).runWith(sink)
      })
  }

  def getChunkedFlowMapAsync(batchSize: Int, sink: SinkType): FlowType = {
    Flow[Measurement]
      .groupedWithin(batchSize, waitForGrouping)
      .mapAsync(parallelism)(collection => {
        println(s"--- write ${collection.size} elements")
        Source(collection).map(measurementAsByteStringLine).runWith(sink)
      })
  }

  def getChunkedFlowMapAsync(batchSize: Int, delay: FiniteDuration, sink: SinkType): FlowType = {
    Flow[Measurement]
      .throttle(elementsToThrottle, delay)
      .groupedWithin(batchSize, waitForGrouping)
      .mapAsync(parallelism)(collection => {
        println(s"--- write ${collection.size} elements")
        Source(collection).map(measurementAsByteStringLine).runWith(sink)
      })
  }

  def getChunkedFlowMapAsyncRoundRobin(batchSize: Int, delay: FiniteDuration, sinks: Seq[SinkType]): FlowType = {
    var idx = 0
    val n = sinks.size

    Flow[Measurement]
      .throttle(elementsToThrottle, delay)
      .groupedWithin(batchSize, waitForGrouping)
      .map(collection => {
        val tuple = (collection, sinks(idx))
        idx = (idx + 1) % n
        tuple
      })
      .mapAsync(parallelism)(tuple => {
        println(s"--- write ${tuple._1.size} elements")
        Source(tuple._1).map(measurementAsByteStringLine).runWith(tuple._2)
      })
  }

  // ======================== SINKS ========================
  private def getSink(path: Path): SinkType = {
    FileIO.toPath(path, Set(WRITE, APPEND))
  }

  // ======================== PIPELINES ========================
  private def pipeline(source: SourceType, flow: FlowTypeSeq, sink: SinkType, elems: Int): Future[IOResult] = {
    source limit (elems - 1) via flow runWith sink
  }

  private def pipeline(source: SourceType, flow: FlowTypeSeq, sink: SinkType): Future[IOResult] = {
    source via flow runWith sink
  }

  private def pipeline(source: SourceType, flow: FlowType, elems: Int): Future[Done] = {
    source limit (elems - 1) via flow runWith Sink.ignore
  }

  private def pipeline(source: SourceType, flow: FlowType): Future[Done] = {
    source via flow runWith Sink.ignore
  }

  // ======================== MEASUREMENT TO (BYTE)STRING ========================
  private def measurementAsByteStringLine(measurement: Measurement): ByteString = {
    ByteString(s"${measurementAsString(measurement)}\n")
  }

  private def measurementSequenceAsByteStringLine(measurements: Seq[Measurement]): ByteString = {
    ByteString(measurements.map(measurementAsStringLine).mkString(""))
  }

  private def measurementAsString(measurement: Measurement) = {
    s"(${measurement._1}, ${measurement._2._2}, Degree ${measurement._2._3}, ${measurement._2._1})"
  }

  private def measurementAsStringLine(measurement: Measurement) = {
    s"${measurementAsString(measurement)}\n"
  }

  // ======================== PROGRAM ========================
  def main(): Unit = {
    println("========== WeatherStationStream App ==========")
    val rand = scala.util.Random

    val fileNames = Seq(
      "streamed_weather_station1.txt",
      "streamed_weather_station2.txt"
    )
    val files = fileNames.map(fileName => Paths.get(s"$basePath/$fileName"))
    val sinks = files.map(getSink)

    val sourceNames = Seq(
      "Linz",
      "Vienna",
      "Dornbirn",
      "New York"
    )

    val generators = sourceNames.map(_ => new MeasurementGenerator(20.2f, 24.15f, TemperatureUnit.Celsius))
    val sources = (0 to generators.size - 1).map(i => getSource(sourceNames(i), generators(i), (minGenerationDelay + rand.nextInt(diffGenerationDelay) + 1).millis))

    val flow = getChunkedFlowMapAsyncRoundRobin(bulkWriteSize, bulkWriteDelay, sinks)
    val futures = Future.sequence(sources.map(source => pipeline(source, flow, elementsToGenerate)))

    Await.ready(futures, Duration.Inf)
    println("========== END WeatherStationStream App ==========")
    Thread.sleep(100)
    system.terminate()
  }

  main()
}
