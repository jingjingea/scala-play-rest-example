package controllers

import javax.inject._

import actor.TestActor
import actor.event.{TestMessage, TopicActor, TopicName}
import akka.actor._
import akka.actor.ActorSystem
import akka.stream.Materializer
import domain.ws.{InEvent, OutEvent}
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import domain.ws.{InEvent, OutEvent}
import play.api.libs.functional.syntax._
import play.api.mvc.WebSocket.FrameFormatter

import scala.concurrent.Future

/**
  * Created by hana on 2017-01-03.
  */

case class WSCommand(cmd: Int, cmd2: Int)

@Singleton
class WebSocketApplication @Inject()(implicit system: ActorSystem, materializer: Materializer) extends Controller {
  implicit val messageFlowTransformer: MessageFlowTransformer[InEvent, OutEvent] = MessageFlowTransformer.jsonMessageFlowTransformer[InEvent, OutEvent]

  implicit val inEventReads: Reads[InEvent] = Json.reads[InEvent]
  implicit val inEventWrites: Writes[InEvent] = Json.writes[InEvent]
  implicit val outEventReads = Json.reads[OutEvent]
  implicit val outEventWrites = Json.writes[OutEvent]

  def socket = WebSocket.acceptOrResult[InEvent, OutEvent] { request =>
    Future.successful(Right(ActorFlow.actorRef(WSActor.props)))
  }
}

class WSActor(out: ActorRef) extends TopicActor {
  override val topics = Seq(TopicName.testTopic)

  val testActor = context.system.actorSelection("/user/MainActor/" + TestActor.name)

  override def receive: Receive = {
    case value: InEvent =>
      println("########")
      out ! Json.obj("message" -> JsString("out message : " + value))
    /*
    case "test" =>
      out ! "test_out_1"
    case "broad" =>
      testActor ! "test"
    case TestMessage(topic, message) =>
      out ! message
    case _ =>
      out ! "test_out_2"
      */
  }
}

object WSActor {
  // API로 통해 WS Actor 접속 시 접속한 client 정보(out)를 같이 넘겨준다.
  def props(out: ActorRef) = Props(new WSActor(out))
}



