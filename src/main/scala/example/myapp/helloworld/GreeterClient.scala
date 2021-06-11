package example.myapp.helloworld

import akka.{Done, NotUsed}
import akka.actor.ActorSystem

import scala.util.{Failure, Success}
import akka.grpc.GrpcClientSettings
import akka.stream.scaladsl.Source
import example.myapp.example.myapp.helloworld.grpc.{GreeterService, GreeterServiceClient, HelloRequest}

import scala.concurrent.Future
import scala.concurrent.duration.DurationInt

object GreeterClient {
  def main(args: Array[String]): Unit = {
    implicit val sys = ActorSystem("HelloWorldClient")
    implicit val ec = sys.dispatcher

    val clientSettings = GrpcClientSettings.connectToServiceAt("127.0.0.1", 8080).withTls(false)

    val client: GreeterService = GreeterServiceClient(clientSettings)

    runSingleRequestReplyExample()
    runStreamRequestExample()
    runStreamingReplyExample()
    runSingleRequestReplyExample()

    sys.scheduler.scheduleWithFixedDelay(1.second, 1.second) {() => runStreamingReplyExample()}

    def runSingleRequestReplyExample(): Unit = {
      sys.log.info("Performing request")
      val reply = client.sayHello(HelloRequest("Alice"))
      reply.onComplete {
        case Success(value) =>
          println(s"got single reply: $value")
        case Failure(exception) =>
          println(s"Error sayHello: $exception")
      }
    }

    def runStreamRequestExample(): Unit = {
      val requests = List("Alice", "Bob", "Carol").map(HelloRequest(_))
      val reply = client.itKeepsTalking(Source(requests))
      reply.onComplete {
        case Success(msg) =>
          println(s"got single reply from streaming requests: $msg")
        case Failure(e) =>
          println(s"Error streamingRequest: $e")
      }
    }

    def runStreamingReplyExample(): Unit = {
      val responseStream = client.itKeepsReplying(HelloRequest("Alice"))
      val done: Future[Done] = responseStream.runForeach(reply => println(s"got streaming reply: ${reply.message}"))

      done.onComplete {
        case Success(_) =>
          println("streamingReply done")
        case Failure(e) =>
          println(s"Error in streamingReply: $e")
      }
    }

    def runStreamingRequestReplyExample(): Unit = {
      val requestStream: Source[HelloRequest, NotUsed] =
        Source
          .tick(100.millis, 1.second, "tick")
          .zipWithIndex
          .map { case (_, i) => i }
          .map(i => HelloRequest(s"Alice-$i"))
          .take(10)
          .mapMaterializedValue(_ => NotUsed)

      val responseStream = client.streamHellos(requestStream)
      val done: Future[Done] =
        responseStream.runForeach(reply => println(s"got streaming reply: ${reply.message}"))

      done.onComplete {
        case Success(_) =>
          println("streamingReply done")
        case Failure(e) =>
          println(s"Error in streamingReply: $e")
      }
    }
  }

}
