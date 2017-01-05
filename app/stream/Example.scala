package stream

/**
  * Created by hana on 2017-01-04.
  */
import akka.stream._
import akka.stream.scaladsl._
import akka.NotUsed
import akka.actor.ActorSystem
import akka.util.ByteString

import scala.concurrent._
import java.nio.file.Paths

import org.slf4j.{Logger, LoggerFactory}

object Example {
  implicit val log: Logger = LoggerFactory.getLogger(getClass)
  /**
    * Create actor system with name, "QuickStart"
    */
  implicit val system = ActorSystem("QuickStart")

  log.info("#################################")
  /**
    * Stream 이 동작하도록 하는 Stream Execution Engine 의 factory 로
    * Materializer 로 생성된 Engine 은 실제 stream 이 수행되도록 해주는 핵심입니다.
    */
  implicit val materializer = ActorMaterializer()

  /**
    * Source 의 첫 번째 type Int 는 Source 가 emit 하게 될 data 의 type 을 의미합니다.
    * 두 번째 type NotUsed 는 Source 가 부가적인 데이터를 의미합니다.
    * 여기에선 1 ~ 100 의 Int 이외에 추가적인 데이터가 없으므로 NotUsed 를 사용하였습니다.
    */
  val source: Source[Int, NotUsed] = Source(1 to 100)

  /**
    * 생성된 Source 아래 runForeach 로 실제 Source 에 대한 검증을 해볼 수 있습니다.
    * Source 를 검증할 때, 위에서 생성한 amterializer 가 implicit 하게 사용됩니다.
    */
  source.runForeach( i => log.info(s" result = $i") )

  /**
    * 1 이라는 숫자로 factorial 계산을 시작해서 source 에서 emit 되는 1 에서 100 의 숫자를
    * scan 하며 factorial 값을 만들어내는 과정을 나타내는 Source 를 만들어 냅니다.
    */
  val factorials: Source[BigInt, NotUsed] = source.scan(BigInt(1))((acc, next) => acc * next)

  // Sink 를 FileIO 로 선택하여 넘어온 값들이 저장될 수 있도록 처리
  val fileSink: Sink[ByteString, Future[IOResult]] = FileIO.toPath(Paths.get("factorials.txt"))

  val result: Future[IOResult] =
    factorials
      .map(num => ByteString(s"$num\n")) // factorials(Source) 에서 생성된 int stream 데이터를 ByteString 형태로 변환
      .runWith(fileSink) // runWith 를 통해서 위에서 정의된 Flow 를 실제 실행해준다.

  def lineSink(fileName: String): Sink[BigInt, Future[IOResult]] =
    Flow[BigInt]
      .map(s => ByteString(s"$s\n"))
      .toMat(FileIO.toPath(Paths.get(fileName)))(Keep.right)

  val reulst: Future[IOResult] = factorials.runWith(lineSink("factorials2.txt"))
}