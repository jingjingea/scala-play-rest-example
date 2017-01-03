package actor.event

import actor.event.EventBusActor.{Subscribe, Unsubscribe}
import actor.event.TopicName.TopicName
import akka.actor.ActorRef
import akka.event.{EventBus, LookupClassification}

/**
  * 2016. 8. 25. - Created by OutOfBedlam@github
  */

final case class LemsEventEnvelope(topic: String, payload: Any)

class LemsEventBus extends EventBus with LookupClassification {
  type Event = LemsEventEnvelope
  type Classifier = String
  type Subscriber = ActorRef

  // 인입된 이벤트에서 classifier를 추출한다.
  override def classify(event: Event): Classifier = event.topic

  // 이벤트가 발생할 때마다 해당 classifier에 가입된 모든 subscriber에 대해서 호출된다.
  override def publish(event: Event, subscriber: Subscriber): Unit = {
    subscriber ! event.payload
  }

  // 전체 subscriber에 대한 순서를 정해야한다.
  override def compareSubscribers(a: Subscriber, b: Subscriber): Int = a.compareTo(b)

  // 내부적으로 사용되는 인덱스 구조체의 초기 크기를 결정한다. (예상되는 classifier의 갯수)
  override def mapSize(): Int = 128
}

object LemsEventBus {
  lazy val lemsbus = new LemsEventBus

  def subscribeMessage(topic: TopicName) = Subscribe(topic.toString)

  def unsubscribeMessage(topic: TopicName) = Unsubscribe(topic.toString)

  def publishMessage(topic: TopicName, payload: Any) = LemsEventEnvelope(topic.toString, payload)

}
