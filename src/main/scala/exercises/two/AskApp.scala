package exercises.two

import akka.actor.{ActorRef, ActorSystem, Props}
import akka.util.Timeout
import exercises.two.ask.ActorRefExtension
import exercises.two.actors.SomeActor
import scala.concurrent.{Await, Future}
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global

object AskApp extends App {
  private val name: String = "AskApp"
  private implicit val timeout: Timeout = 1.seconds

  private implicit def extendActorRef(actor: ActorRef): ActorRefExtension = {
    new ActorRefExtension(actor)
  }

  def askTest(actorRef: ActorRef, title: String): Unit = {
    println(s"=== ${title} ===")

    val future: Future[Any] = actorRef ? SomeActor.Message()
    future.failed.foreach(ex => println(s"!!!FAILED: $ex !!!"))
    future.foreach(message =>   println(s"SUCCESS received: '$message'"))

    Await.ready(future, Duration.Inf)
    Thread.sleep(50)
    println(s"=== END ${title} ===")
  }

  def main(): Unit = {
   println(s"========== $name ==========")
    val system = ActorSystem("AskSystem")

    val successActor = system.actorOf(Props(classOf[SomeActor], 500.millis), "SuccessActor")
    val failActor = system.actorOf(Props(classOf[SomeActor], 2000.millis), "FailActor")

    askTest(successActor, "Test Success Case")
    askTest(failActor, "Test Failure Case")

    Thread.sleep(50)
    Await.ready(system.terminate(), Duration.Inf)
    println(s"========== END $name ==========")
  }
  main()
}
