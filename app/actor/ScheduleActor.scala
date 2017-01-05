package actor

import akka.actor._

/**
  * Created by hanseung on 2017-01-05.
  */
class ScheduleActor extends Actor {
  override def receive = {
    case "msg" =>
      println("print msg per 30 seconds")
  }

}

object ScheduleActor {
  val props = Props[ScheduleActor]
  val name = this.getClass.getSimpleName
}
