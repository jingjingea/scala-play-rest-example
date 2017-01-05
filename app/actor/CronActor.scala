package actor

import javax.inject.Inject

import actor.event.EventBusActor
import akka.actor._
import com.typesafe.akka.extension.quartz.QuartzSchedulerExtension
import domain.ws.InEvent
import play.api._
import play.api.libs.json.Json

/**
  * Created by hanseung on 2017-01-05.
  */
class CronActor extends Actor {

  println("!!!!!!!!!!")
  val scheduler = QuartzSchedulerExtension(context.system)
  println("##########")

  val busActor = context.system.actorSelection("/user/MainActor/" + EventBusActor.name)
  val cronJson = InEvent("CRON", "cronTest", "12345678")

  println(cronJson)
  val temp = scheduler.schedule("cronEvery3Seconds", busActor, cronJson)

  override def receive: Receive = {
    case _: String =>
      println("nononono")
  }

}

object CronActor {
  val name = this.getClass.getSimpleName
  val props = Props[CronActor]
}