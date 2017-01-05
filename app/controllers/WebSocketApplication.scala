package controllers

import javax.inject._

import actor.TestActor
import actor.event.{EventBusActor, TestMessage, TopicActor, TopicName}
import actor.event.EventBusActor._
import akka.actor._
import akka.actor.ActorSystem
import akka.stream.Materializer
import domain.user.UserInfo
import play.api.libs.json._
import play.api.libs.streams.ActorFlow
import play.api.mvc.WebSocket.MessageFlowTransformer
import play.api.mvc._
import domain.ws.{InEvent, OutEvent}

import scala.concurrent.Future

/**
  * Created by hana on 2017-01-03.
  */

case class WSCommand(cmd: Int, cmd2: Int)

@Singleton
class WebSocketApplication @Inject()(implicit system: ActorSystem, materializer: Materializer) extends Controller {
  implicit val inEventFormat = Json.format[InEvent]
  implicit val outEventFormat = Json.format[UserInfo]

  implicit val messageFlowTransformer: MessageFlowTransformer[InEvent, UserInfo] = MessageFlowTransformer.jsonMessageFlowTransformer[InEvent, UserInfo]

  def socket = WebSocket.acceptOrResult[InEvent, UserInfo] { request =>
    Future.successful(Right(ActorFlow.actorRef(WSActor.props)))
  }
}

class WSActor(out: ActorRef) extends TopicActor {
  println("before topics")
  override val topics = Seq(TopicName.testTopic)
  topics.map(x => {println("##### check topics"); println("### " + x)})

  val testActor = context.system.actorSelection(COMMON_ACTOR_PATH + TestActor.name)
  // override val busActor = context.system.actorSelection(COMMON_ACTOR_PATH + EventBusActor.name)

  implicit val inEventFormat = Json.format[InEvent]

  override def receive: Receive = {
    case value: InEvent =>
      val json = Json.toJson(value)
      (json \ "command").as[String] match {
        case "SUB" => subscribeTopic((json \ "topic").as[String])
        case "UNSUB" => unsubscribeTopic((json \ "topic").as[String])
        case "SEND" =>
          val topic = (json \ "topic").as[String]
          val message = (json \ "message").as[String]
          sendMessage(TestMessage(TopicName.getTopicName(topic), message))
      }
      testActor ! "test"
    case TestMessage(topic, message) =>
      out ! UserInfo(message, "", "", Some(""), Some(""), 0L, "", 0L)
    case user: UserInfo =>
      out ! user
    case _ =>
      out ! OutEvent("Unknown")
  }

  def subscribeTopic(topic: String) = {
    busActor ! Subscribe(topic)
  }

  def unsubscribeTopic(topic: String) = {
    busActor ! Unsubscribe(topic)
  }

  def sendMessage(tm: TestMessage) = {
    busActor ! tm
  }

}

object WSActor {
  // API로 통해 WS Actor 접속 시 접속한 client 정보(out)를 같이 넘겨준다.
  def props(out: ActorRef) = Props(new WSActor(out))
}



