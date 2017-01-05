
import actor.MainActor
import controllers.ScheduleApplication
import org.slf4j.{Logger, LoggerFactory}
import play.api._


object Global extends GlobalSettings {
  implicit val log: Logger = LoggerFactory.getLogger(getClass)
  override def onStart(app: Application) {
    log.info("Application has started")
    mydb.MyDatabase.open()
    val system = app.actorSystem
    val mainActor = system.actorOf(MainActor.props, "MainActor")
    mainActor ! MainActor.Initialize
  }

  override def onStop(app: Application) {
    log.info("Application shutdown...")
    mydb.MyDatabase.close()

    val system = app.actorSystem
    val mainActor = system.actorSelection("/user/`")
    mainActor ! MainActor.Terminate

  }
}