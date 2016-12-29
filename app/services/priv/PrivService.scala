package services.priv

import domain.priv._
import mydb.MyDatabase._
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by user on 2016-12-26.
  */
trait PrivServiceComponent {
  val privService: PrivService

  trait PrivService {
    def createPriv(priv: Priv): Future[Int]

    def updatePriv(id: Long, priv: Priv)

    def deletePriv(id: Long)
  }

}

trait PrivServiceComponentImpl extends PrivServiceComponent {
  override val privService = new PrivServiceImpl

  class PrivServiceImpl extends PrivService {
    override def createPriv(priv: Priv): Future[Int] = {
      lemsdb.run(PrivTable += priv)
    }

    override def updatePriv(id: Long, priv: Priv) = {
      lemsdb.run(PrivTable.filter(_.privId === id).update(priv))
    }

    override def deletePriv(id: Long) = {
      lemsdb.run(PrivTable.filter(_.privId === id).delete)
    }

  }

}
