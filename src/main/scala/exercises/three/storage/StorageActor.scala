package exercises.three.storage

import java.nio.file.Paths

import akka.actor.{Actor, ActorSystem}
import exercises.three.weatherstation.WeatherStationActor.{Message, Stop}
import akka.stream.scaladsl._
import akka.util.ByteString
import java.nio.file.StandardOpenOption._
import java.time.Duration

import akka.NotUsed

import scala.collection.immutable.Queue
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

object StorageActor {
  case class Flush()
}

class StorageActor(val storageName: String, val fileName: String, val bufferThreshold: Int = 5, val bufferWriteIntervalMs: Int = 2000, val forcedDelayMode: Boolean = false, val forcedDelayMs: Int = 100) extends Actor {
  import StorageActor.Flush

  private implicit val system: ActorSystem = context.system
  private var queue = Queue.empty[String]
  private val file = Paths.get(s"./src/main/scala/exercises/three/storage/$fileName")
  private var stop = false
  private var noMessages = 0

  private val intervalDuration: Duration = Duration.ofMillis(bufferWriteIntervalMs)
  context.system.scheduler.scheduleWithFixedDelay(intervalDuration, intervalDuration, () => { self ! Flush() }, context.system.dispatcher)

  private def println(message: Any): Unit = {
    printAsync(s"$storageName => $message (thread id=${Thread.currentThread.getId})")
  }
  private def printAsync(msg: Any): Unit = {
    Future { Console.println(s"$msg") }
  }

  override def receive: Receive = {
    case Message(name, measurement) =>
      println(s"Message '$name:$measurement' Received")
      if (forcedDelayMode) {
        Thread.sleep(forcedDelayMs)
      }
      queue = queue.enqueue(s"(${name}, ${measurement._2} Degree ${measurement._3}, ${measurement._1})\n")
      tryWriteThreshold()
      noMessages += 1

    case Stop() =>
      stop = true
    case Flush() =>
      flush()
      if (stop) {
        println(s"+++ $noMessages messages processed")
        context.stop(self)
      }
  }

  override def unhandled(message: Any): Unit = {
    println(s"   === UNHANDLED: ${self.path.name}: '$message'")
  }

  private def writeList(source: Source[String, NotUsed]) = {
    val result = source.map(str => ByteString(str)).runWith(FileIO.toPath(file, Set(WRITE, APPEND)))
    result.failed.foreach(ex => println(s"!!!Bulk Write FAILED\n$ex"))
  }

  private def tryWriteThreshold(): Unit = {
    val elems = tryGetThresholdElements()
    if (elems != null) {
      writeList(Source(elems))
      println(s"THRESHOLD reached: $bufferThreshold ELEMENTS WRITTEN")
    }
  }

  private def flush(): Unit = {
    writeList(Source(getElements()))
    println("BUFFER FLUSHED")
  }

  private def tryGetThresholdElements() = {
      if (queue.size >= bufferThreshold) {
        val splitted: (Queue[String], Queue[String]) = queue.splitAt(bufferThreshold)
        queue = splitted._1
        splitted._2
      }
      else {
        null
      }
  }

  private def getElements(): List[String] = {
      val elems = queue.toList
      queue = Queue.empty
      elems
  }
}
