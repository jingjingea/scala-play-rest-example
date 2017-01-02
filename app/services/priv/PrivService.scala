package services.priv

import domain.priv._
import domain.role.RoleTable
import mydb.MyDatabase._
import slick.dbio.Effect.Read
import slick.driver.PostgresDriver.api._
import slick.profile.FixedSqlStreamingAction

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by user on 2016-12-26.
  */
trait PrivServiceComponent {
  val privService: PrivService

  trait PrivService {
    def getList(limit: Option[Int], offset: Option[Int], sIdx: Option[String], sOrder: Option[String], name: Option[String]): Future[(Seq[Priv], Int)]

    def createPriv(priv: Priv): Future[Int]

    def updatePriv(id: Long, priv: Priv)

    def deletePriv(id: Long)

  }

}

trait PrivServiceComponentImpl extends PrivServiceComponent {
  override val privService = new PrivServiceImpl

  class PrivServiceImpl extends PrivService {
    final val ORDER_ASC = "ASC"
    final val ORDER_DESC = "DESC"

    override def getList(limit: Option[Int], offset: Option[Int], sIdx: Option[String], sOrder: Option[String], name: Option[String]): Future[(Seq[Priv], Int)] = {
      var commonQuery = for {
        priv <- PrivTable
      } yield priv // 단순히 case class를 Query 타입으로 변경하기 위함 for 질의

      if (name.isDefined) commonQuery = commonQuery.filter { priv => priv.name.toUpperCase like "%" + name.get.toUpperCase + "%" }

      var pagingQuery = commonQuery.sortBy { priv =>
        sIdx match {
          case Some("name") => if (sOrder.getOrElse("").equals(ORDER_DESC)) priv.name.desc else priv.name.asc
          case _ => priv.privId.desc
        }
      }

      if (offset.isDefined && limit.isDefined) {
        pagingQuery = pagingQuery.drop(offset.get).take(limit.get)
      }

      val query = for {
        rows: Seq[Priv] <- pagingQuery.result
        totalRows <- commonQuery.length.result
      } yield (rows, totalRows)

      lemsdb.run(query)
    }

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
