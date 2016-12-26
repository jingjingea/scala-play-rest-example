package services.priv

import domain.priv._
import mydb.MyDatabase._
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

/**
  * Created by user on 2016-12-26.
  */
trait PrivServiceComponent {
  val privService: PrivService

  trait PrivService {
    def createPriv(role: Priv): Future[Int]
  }

}

trait PrivServiceComponentImpl extends PrivServiceComponent {
  override val privService = new PrivServiceImpl

  class PrivServiceImpl extends PrivService {
    override def createPriv(priv: Priv): Future[Int] = {
      lemsdb.run(PrivTable += priv)
    }

  }
}
