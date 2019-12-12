package mpv.basics.reactivestreams

import akka.actor.ActorSystem
import akka.stream.scaladsl._
import akka.{Done, NotUsed}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}

object ReactiveStreams extends App {

  implicit val system: ActorSystem = ActorSystem("MySystem")

  def isPrime(n: Long): Boolean = {
    val upper = math.sqrt(n).toInt + 1
    (2 until upper) forall (i => n % i != 0)
  }

  def trace[T](expr: => T): T = {
    val result = expr
    println(s"$result (thread=${Thread.currentThread.getId})")
    result
  }

  def tracef[T](expr: => T, formatString: String = "%s"): T = {
    val result = expr
    println((formatString + " (thread=%d)")
      .format(result, Thread.currentThread.getId))
    result
  }

  def computePrimes(): Unit = {
    val source: Source[Int, NotUsed] = Source(2 to 200)
    val flow: Flow[Int, Int, NotUsed] = Flow[Int].filter(isPrime(_))
    val sink: Sink[Int, Future[Done]] = Sink.foreach[Int](i => print(s"$i "))

    val s1: Source[Int, NotUsed] = source via flow
    val g: RunnableGraph[NotUsed] = source.via(flow).to(sink)
    g.run()
    Thread.sleep(100)
    println()
    val g2: RunnableGraph[NotUsed] = source via flow to sink
    g2.run()
    Thread.sleep(100)
    println()

    val g3: RunnableGraph[Future[Done]] = source.via(flow).toMat(sink)(Keep.right)
    val done = g3.run()
    Await.ready(done, Duration.Inf)
    println()
  }

  def computePrimesAsync(): Unit = {
    val source: Source[Int, NotUsed] = Source(2 to 200)
    val primeNumberSink: Sink[Int, Future[Done]] = Sink.foreach[Int](i => print(s"$i "))

    //val done1 = source.filter(isPrime(_)).runWith(primeNumberSink)
    //Await.ready(done1, Duration.Inf)
    //println()

    //val done2 = source
    //  .filter(i => { trace(i); isPrime(i) })
     // .runWith(primeNumberSink)

    //Await.ready(done2, Duration.Inf)
    //println()

    //val done3 =
     // source
      //    .throttle(1, 50.millis)
       //   .async
         // .filter(i => { trace(i); isPrime(i) })
          //.runWith(primeNumberSink)

    //Await.ready(done3, Duration.Inf)
    //println()
    val noOfThreads = 6

    val done4 =
      source
        .mapAsync(noOfThreads)(i => Future { trace((i, isPrime(i))) })
        .collect({ case (i, true) => i })
        .runWith(primeNumberSink)

    Await.ready(done4, Duration.Inf)
    println()
  }

  println("----------------- computePrimes -----------------")
  computePrimes()

  println("-------------- computePrimesAsync ---------------")
  computePrimesAsync()

  system.terminate()
}
