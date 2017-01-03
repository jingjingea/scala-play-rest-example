package actor

import actor.event._
import akka.actor.{Actor, Props}
import org.slf4j.{Logger, LoggerFactory}

/**
  * Created by hana on 2017-01-03.
  */
class MainActor extends Actor {
  implicit val log: Logger = LoggerFactory.getLogger(getClass)

  override def receive: Receive = {
    case MainActor.Initialize => onInitialize()
    case MainActor.Terminate => onTermiate()
    case _ => log.warn("received unknown message")
  }

  private def onInitialize(): Unit = {
    log.info("Initialized....")
    val eventBusActor = context.actorOf(EventBusActor.props, name = EventBusActor.name)
    val testActor = context.actorOf(TestActor.props, name = TestActor.name)

    log.info(
      s"""
         | Actors
         | -------------------------------------------------
         |    ${EventBusActor.name}=${eventBusActor.path}
         |    ${TestActor.name}=${testActor.path}
         """.stripMargin)
  }

  private def onTermiate(): Unit = {
    log.info("Terminating...")
    context.system.terminate()
  }
}

object MainActor {
  val props = Props[MainActor]

  case object Initialize

  case object Terminate

}

