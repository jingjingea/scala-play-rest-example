package stream

import java.nio.file.Paths

import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.Tcp.{IncomingConnection, ServerBinding}
import akka.stream.scaladsl._
import akka.util.ByteString
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent._


object Example2 {
  implicit val log: Logger = LoggerFactory.getLogger(getClass)

  implicit val system = ActorSystem("QuickStart")
  log.info("######### call Example2")
  /**
    * Stream 이 동작하도록 하는 Stream Execution Engine 의 factory 로
    * Materializer 로 생성된 Engine 은 실제 stream 이 수행되도록 해주는 핵심입니다.
    */
  implicit val materializer = ActorMaterializer()

  val connections: Source[IncomingConnection, Future[ServerBinding]] = Tcp().bind("localhost", 8888)
  val fileSink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(Paths.get("test.txt"))
  connections runForeach { connection =>
    log.info(s"##########New connection from: ${connection.remoteAddress}")


    val echo = Flow[ByteString]
      .via(Framing.delimiter(
        ByteString("\n"),
        maximumFrameLength = 256,
        allowTruncation = true))
      .map(_.utf8String)
      .map{ rep =>
        log.info("rep : " + rep)
        rep + "!!!\n"
      }
      .map(ByteString(_)).alsoTo(fileSink)

    connection.handleWith(echo)
  }
}