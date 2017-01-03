package actor.event

import akka.actor.ActorRef
import actor.event.TopicName.TopicName
/**
  * 2016. 9. 23. - Created by OutOfBedlam@github
  */

trait TopicMessage {
  def topic: TopicName
}

case class TestMessage(topic : TopicName, message : String)
  extends Serializable
    with TopicMessage

