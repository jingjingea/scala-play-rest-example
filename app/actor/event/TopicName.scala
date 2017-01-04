package actor.event

/**
  * 2016. 9. 23. - Created by OutOfBedlam@github
  */
object TopicName extends Enumeration {
  type TopicName = Value

  val testTopic = Value("lems.test")
  def getTopicName(topic: String) = Value(topic)
}
