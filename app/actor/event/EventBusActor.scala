package actor.event

import actor.event.EventBusActor._
import actor.event.LemsEventBus._
import akka.actor.{Actor, Props, Terminated}
import controllers.WSActor
import controllers.WSActor._
import domain.ws.InEvent
import org.slf4j.{Logger, LoggerFactory}
import play.api.libs.json.Json

/**
  * 2016. 8. 25. - Created by OutOfBedlam@github
  */

class EventBusActor extends Actor {
  implicit val log: Logger = LoggerFactory.getLogger(getClass)

  override def receive: Receive = {
    case Subscribe(topic) =>
      println("when? $$$$$$$$$$$$$$")
      val remoteActor = sender() // sender(): ActorRef, 이 Actor로 메세지 보낸 마지막 Actor
      println(sender())

      if (lemsbus.subscribe(remoteActor, topic)) {
        context.watch(remoteActor) // EventBusActor는 watch를 통해 sender Actor가 terminate되는 것을 알 수 있음
        log.info(s"Subscribe success - topic: $topic from: $remoteActor")
      }
      else {
        log.warn(s"Subscribe failure - topic: $topic from: $remoteActor")
      }

    case Unsubscribe(topic) =>
      val remoteActor = sender()
      lemsbus.unsubscribe(remoteActor, topic)

    case t: Terminated =>
      log.info(s"Unsubscribe: $t")
      lemsbus.unsubscribe(t.actor)

    case tm: TopicMessage =>
      println("topic message")
      if (log.isDebugEnabled)
        log.debug(s"TopicMessage: topic=${tm.topic}")
      lemsbus.publish(LemsEventEnvelope(tm.topic.toString, tm)) // Event의 type LemsEventEnvelope
      log.info(lemsbus.classify(LemsEventEnvelope(tm.topic.toString, tm)))

    case croning: InEvent =>
      implicit val inEventFormat = Json.format[InEvent]
      val cronJson = Json.toJson(croning)
      val topic = (cronJson \ "topic").as[String]
      val message = (cronJson \ "message").as[String]
      log.info(s"###########################################################################")
      lemsbus.publish(LemsEventEnvelope(topic, message))
  }
}

object EventBusActor {
  val props = Props[EventBusActor]
  val name = this.getClass.getSimpleName

  case class Subscribe(topic: String)

  case class Unsubscribe(topic: String)

}