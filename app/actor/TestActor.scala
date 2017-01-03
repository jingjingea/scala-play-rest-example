package actor

import actor.event.{EventBusActor, TestMessage, TopicActor, TopicName}
import akka.actor.{Actor, Props}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by hana on 2017-01-03.
  */
class TestActor extends Actor{
  final val COMMON_ACTOR_PATH = "/user/MainActor/"
  final val ACTOR_PATH_EVENTBUS = COMMON_ACTOR_PATH + EventBusActor.name
  val busActor = context.system.actorSelection(ACTOR_PATH_EVENTBUS)

  implicit val log: Logger = LoggerFactory.getLogger(getClass)

  override def receive: Receive = {
    case "test" =>
      log.info("get test")
      busActor ! TestMessage(TopicName.testTopic, "test message")
    case _ =>
      log.info("unknown topic")
  }
}

object TestActor {
  val props = Props[TestActor]
  val name = this.getClass.getSimpleName
}
