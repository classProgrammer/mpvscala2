package exercises.three.storage

import java.nio.file.Paths

import akka.actor.{Actor, ActorSystem}
import exercises.three.weatherstation.WeatherStationActor.{Message, QueueEmpty, Stop}
import akka.stream.scaladsl._
import akka.util.ByteString
import java.nio.file.StandardOpenOption._
import java.time.Duration

import akka.NotUsed

import scala.collection.mutable
import scala.concurrent.ExecutionContext.Implicits.global

object StorageActor {
  case class Flush()
}

class StorageActor(val storageName: String, val fileName: String, val bufferThreshold: Int = 5, val bufferWriteIntervalMs: Int = 2000, val forcedDelayMode: Boolean = false, val forcedDelayMs: Int = 100) extends Actor {
  import StorageActor.Flush

  private implicit val system: ActorSystem = context.system
  private val queue = mutable.Queue.empty[String]
  private val file = Paths.get(s"C:\\Users\\GeraldSpenlingwimmer\\IdeaProjects\\mpvscala2\\src\\main\\scala\\exercises\\three\\storage\\$fileName")
  private var stop = false
  private var noMessages = 0

  private val intervalDuration: Duration = Duration.ofMillis(bufferWriteIntervalMs)
  context.system.scheduler.scheduleWithFixedDelay(intervalDuration, intervalDuration, () => { self ! Flush() }, context.system.dispatcher)

  private def println(msg: Any) = Console.println(s"$storageName => $msg (thread id=${Thread.currentThread.getId})")

  override def receive: Receive = {
    case Message(name, measurement) =>
      println("Message Received")
      if (forcedDelayMode) {
        Thread.sleep(forcedDelayMs)
      }
      queue += s"(${name}, ${measurement._2} Degree ${measurement._3}, ${measurement._1})\n"
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
      println("THRESHOLD ELEMENTS WRITTEN")
    }
  }

  private def flush(): Unit = {
    writeList(Source(getElements()))
    println("BUFFER FLUSHED")
  }

  private def tryGetThresholdElements() = {
      if (queue.size >= bufferThreshold) {
        val elems = queue.take(bufferThreshold).toList
        queue.remove(0, bufferThreshold)
        elems
      }
      else {
        null
      }
  }

  private def getElements(): List[String] = {
      val elems = queue.toList
      queue.clear()
      elems
  }
}
