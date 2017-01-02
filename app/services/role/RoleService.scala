package services.role

import domain.role._
import domain.priv._
import domain.rolePrivLst._
import mydb.MyDatabase._
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by user on 2016-12-26.
  */
trait RoleServiceComponent {
  val roleService: RoleService

  trait RoleService {
    def getList(limit: Option[Int], offset: Option[Int], sIdx: Option[String], sOrder: Option[String], name: Option[String], privId: Option[Long]): Future[(Seq[(Role, Priv)], Int)]

    def createRole(role: Role, privIdList: Seq[Long]): Unit

    def updateRole(id: Long, role: Role)

    def deleteRole(id: Long)
  }

}

trait RoleServiceComponentImpl extends RoleServiceComponent {
  override val roleService = new RoleServiceImpl

  class RoleServiceImpl extends RoleService {
    final val ORDER_ASC = "ASC"
    final val ORDER_DESC = "DESC"

    override def getList(limit: Option[Int], offset: Option[Int], sIdx: Option[String], sOrder: Option[String], name: Option[String], privId: Option[Long]): Future[(Seq[(Role, Priv)], Int)] = {
      var commonQuery = for {
        (role, rolePrivList) <- for {
          role <- RoleTable
          rolePrivList <- RolePrivLstTable.filter(_.roleId === role.roleId)
        } yield (role, rolePrivList)
        priv <- for {
          priv <- PrivTable.filter(_.privId === rolePrivList.privId)
        } yield priv
      } yield (role, priv)

      if (name.isDefined) commonQuery = commonQuery.filter { case (role, _) => role.name.toUpperCase like "%" + name.get.toUpperCase + "%" }
      if (privId.isDefined) commonQuery = commonQuery.filter { case (_, priv) => priv.privId === privId }

      var pagingQuery = commonQuery.sortBy {
        case (role, _) => sIdx match {
          case Some("name") => if (sOrder.getOrElse("").equals(ORDER_DESC)) role.name.desc else role.name.asc
          case _ => role.roleId.desc
        }
      }

      if (limit.isDefined && offset.isDefined) {
        pagingQuery = pagingQuery.drop(offset.get).take(limit.get)
      }

      val query = for {
        rows <- pagingQuery.result
        totalRows <- commonQuery.length.result
      } yield (rows, totalRows)

      lemsdb.run(query)
    }

    override def createRole(role: Role, privIdList: Seq[Long]): Unit = {
      val createRolePrivQueryList = for {
        newRoleId <- RoleTable returning RoleTable.map(_.roleId) += role
        _ <- RolePrivLstTable ++= privIdList.map((privId: Long) => RolePrivLst(newRoleId, privId))
      } yield newRoleId

      lemsdb.run(createRolePrivQueryList.transactionally)
    }

    override def updateRole(id: Long, role: Role) = {
      lemsdb.run(RoleTable.filter(_.roleId === id).update(role))
    }

    override def deleteRole(id: Long) = {
      lemsdb.run(RoleTable.filter(_.roleId === id).delete)
    }
  }

}
