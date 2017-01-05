package stream

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._
import akka.util.ByteString
import java.nio.file._

import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Future
import scala.concurrent.duration._

object Client extends App{
  implicit val log: Logger = LoggerFactory.getLogger(getClass)
  implicit val system = ActorSystem("client")
  val serverConnection = Tcp().outgoingConnection("127.0.0.1", 9982)
  val path = "transactions.csv"
  val readLines: Source[ByteString, Future[IOResult]] =
  FileIO.fromPath(Paths.get(path))
        .via(Framing.delimiter(ByteString(System.lineSeparator), maximumFrameLength = 512, allowTruncation = true))
  val logWhenComplete = Sink.onComplete(r => log.info("Transfer complete: " + r))

  val graph = RunnableGraph.fromGraph(GraphDSL.create() { implicit b =>
    import GraphDSL.Implicits._
    readLines ~> serverConnection ~> logWhenComplete
    ClosedShape
  })
  implicit val mat = ActorMaterializer()
  graph.run()
}



