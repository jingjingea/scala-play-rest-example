
import org.slf4j.{Logger, LoggerFactory}
import play.api._


object Global extends GlobalSettings {
  implicit val log: Logger = LoggerFactory.getLogger(getClass)
  override def onStart(app: Application) {
    log.info("Application has started")
    mydb.MyDatabase.open()
  }

  override def onStop(app: Application) {
    log.info("Application shutdown...")
    mydb.MyDatabase.close()
  }
}