package actor.event

import actor.event.EventBusActor._
import actor.event.LemsEventBus._
import akka.actor.{Actor, Props, Terminated}
import org.slf4j.{Logger, LoggerFactory}

/**
  * 2016. 8. 25. - Created by OutOfBedlam@github
  */

class EventBusActor extends Actor {
  implicit val log: Logger = LoggerFactory.getLogger(getClass)

  override def receive: Receive = {
    case Subscribe(topic) =>
      val remoteActor = sender()

      if (lemsbus.subscribe(remoteActor, topic)) {
        context.watch(remoteActor)
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
      if (log.isDebugEnabled)
        log.debug(s"TopicMessage: topic=${tm.topic}")
      lemsbus.publish(LemsEventEnvelope(tm.topic.toString, tm))
  }
}

object EventBusActor {
  val props = Props[EventBusActor]
  val name = this.getClass.getSimpleName

  case class Subscribe(topic: String)

  case class Unsubscribe(topic: String)

}