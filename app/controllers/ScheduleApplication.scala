package controllers

import java.util.{Date, TimeZone}

import actor.ScheduleActor
import akka.actor._
import com.typesafe.akka.extension.quartz.{QuartzSchedule, QuartzSchedulerExtension, QuartzSchedules}
import com.typesafe.config.ConfigFactory
import play.api.mvc.Controller

/**
  * Created by hanseung on 2017-01-05.
  *
  */
class ScheduleApplication {
  val actorSystem = ActorSystem("cronTest")

  val configSchedule = {
    ConfigFactory.parseString(
      """
        schedules {
          cronEvery30Seconds {
            description = "A cron job that fires off every 30 seconds"
            expression = "*/30 * * ? * *"
            calendar = "CronOnlyBusinessHours"
          }
          cronEvery10Seconds {
            description = "A cron job that fires off every 10 seconds"
            expression = "*/10 * * ? * *"
          }
        }
      """.stripMargin)
  }

  lazy val schedules = QuartzSchedules(configSchedule, TimeZone.getTimeZone("UTC"))
  val scheduler = QuartzSchedulerExtension(actorSystem)
  val schedulerActor = actorSystem.actorOf(ScheduleActor.props, ScheduleActor.name)

  def startScheduler: Date = {
    // scheduler.schedules(configSchedule, TimeZone.getTimeZone("UTC"))
    scheduler.schedule("cronEvery10Seconds", schedulerActor, "msg")
  }

}

object ScheduleApplication {
  val scheduler = new ScheduleApplication
}