package controllers

import javax.inject._

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.stream.Materializer
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._

/**
  * Created by hana on 2017-01-03.
  */
@Singleton
class WebSocketApplication  @Inject()(implicit system: ActorSystem, materializer: Materializer) extends Controller {

  implicit val inEventFormat = Json.format[WSActor.WSCommand]
  implicit val messageFlowTransformer = MessageFlowTransformer.jsonMessageFlowTransformer[WSActor.WSCommand, JsValue]

  def socket = WebSocket.accept[WSActor.WSCommand, JsValue] { implicit request =>
    ActorFlow.actorRef(WSActor.props)
  }
}

class WSActor(out: ActorRef) extends Actor {
  override def receive: Receive = {
    case cmd: WSActor.WSCommand =>
  }
}

object WSActor {
  case class WSCommand(cmd: String)
  def props(out: ActorRef) = Props(new WSActor(out))
  // API로 통해 WS Actor 접속 시 접속한 client 정보(out)를 같이 넘겨준다.
  val name = this.getClass.getSimpleName
}
