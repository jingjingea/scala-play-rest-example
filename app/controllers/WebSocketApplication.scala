package controllers

import javax.inject._

import akka.actor._
import akka.stream.Materializer
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._

import scala.concurrent.Future

/**
  * Created by hana on 2017-01-03.
  */

case class WSCommand(cmd: Int, cmd2 : Int)

@Singleton
class WebSocketApplication  @Inject()(implicit system: ActorSystem, materializer: Materializer) extends Controller {

  def socket = WebSocket.acceptOrResult[String, String] { implicit request =>
    Future.successful(Right(ActorFlow.actorRef(WSActor.props)))
  }
}

class WSActor(out: ActorRef) extends Actor {
  override def receive: Receive = {
    case "test" =>
      out ! "test_out_1"
    case _ =>
      out ! "test_out_2"
  }
}

object WSActor {
  // API로 통해 WS Actor 접속 시 접속한 client 정보(out)를 같이 넘겨준다.
  def props(out: ActorRef) = Props(new WSActor(out))
}
