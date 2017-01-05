package stream

import java.nio.file.Paths
import java.util.{Calendar, Date}

import akka.{Done, NotUsed}
import akka.actor.ActorSystem
import akka.stream.{ActorMaterializer, IOResult}
import akka.stream.scaladsl.Tcp.IncomingConnection
import akka.stream.scaladsl.{FileIO, Framing, Sink, Source, Tcp}
import akka.util.ByteString
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Future

case class Transaction(id: String, accountNo: String, debitCredit: String, amount: BigDecimal, date: Date = Calendar.getInstance.getTime)

object Transaction {
  def apply(fields: Array[String]): Option[Transaction] = {
    Some(this (fields(0), fields(1), fields(2), BigDecimal(fields(3)))) // @todo: exception handling
  }
}


class Server(host: String, port: Int)(implicit val system: ActorSystem) {
  implicit val log: Logger = LoggerFactory.getLogger(getClass)

  def run(): Unit = {
    val fileSink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(Paths.get("test.txt"))
    log.info("################### call server2")
    implicit val mat = ActorMaterializer()
    log.info(s"Receiver: binding to $host:$port")
    Tcp().bind(host, port).runForeach { (conn: IncomingConnection) =>
      log.info("################### call server3")
      val test: Sink[Transaction, Future[Done]] = Sink.foreach[Transaction]{ (rsp: Transaction) => log.info(s"$rsp")}
      val receiveSink =
        conn.flow
          .via(Framing.delimiter(ByteString(System.lineSeparator), maximumFrameLength = 512, allowTruncation = true)).map(_.utf8String)
          .map{rsp => log.info(rsp)
            rsp.split(",")
          }
          .mapConcat{rsp => log.info(s"$rsp")
            Transaction(rsp).toList}
          .to(fileSink)

      receiveSink.runWith(Source.empty)
    }
  }
}

