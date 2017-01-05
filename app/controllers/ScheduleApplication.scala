package controllers

import java.util.Date

import actor.ScheduleActor
import akka.actor._
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension

/**
  * Created by hanseung on 2017-01-05.
  *
  */
class ScheduleApplication {
  val actorSystem = ActorSystem("cronTest")

  val scheduler = QuartzSchedulerExtension(actorSystem)
  val schedulerActor = actorSystem.actorOf(ScheduleActor.props, ScheduleActor.name)


  def startScheduler: Date = {
    scheduler.schedule("cronEvery3Seconds", schedulerActor, "msg")
  }

}

object ScheduleApplication {
  val schedulerApplication = new ScheduleApplication
}