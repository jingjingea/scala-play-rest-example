package actor.event

import akka.actor.Actor
import org.slf4j.{Logger, LoggerFactory}
import actor.event.EventBusActor.{Subscribe, Unsubscribe}
import actor.event.TopicName.TopicName
/**
  * 2016. 9. 23. - Created by OutOfBedlam@github
  */
abstract class TopicActor extends Actor{
  final val COMMON_ACTOR_PATH = "/user/MainActor/"
  final val ACTOR_PATH_EVENTBUS = COMMON_ACTOR_PATH + EventBusActor.name

  implicit val log: Logger = LoggerFactory.getLogger(getClass)
  val busActor = context.system.actorSelection(ACTOR_PATH_EVENTBUS)

  def subscribe(topic: TopicName): Unit = {
    busActor ! Subscribe(topic.toString)
  }

  def unsubscribe(topic: TopicName): Unit = {
    busActor ! Unsubscribe(topic.toString)
  }

  val topics:Seq[TopicName] = Seq.empty

  override def preStart(): Unit = {
    super.preStart()
    topics.foreach(subscribe)
  }

  override def postStop(): Unit = {
    topics.foreach(unsubscribe)
    super.postStop()
  }
}
