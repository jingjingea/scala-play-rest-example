
import play.api._


object Global extends GlobalSettings {

  override def onStart(app: Application) {
    Logger.info("Application has started")
    mydb.MyDatabase.open()
  }

  override def onStop(app: Application) {
    Logger.info("Application shutdown...")
    mydb.MyDatabase.close()
  }
}