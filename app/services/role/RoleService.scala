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
    def getList(limit: Option[Int], offset: Option[Int], sIdx: Option[String], sOrder: Option[String], name: Option[String], privId: Option[Long]): Future[(Seq[Role], Seq[(Long, Priv)], Int)]

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

/*
    private def selectByCondition(limit: Option[Int], offset: Option[Int], sIdx: Option[String], sOrder: Option[String], name: Option[String], selectedPrivIdArr : Option[Seq[Long]]) = {
      var commonQuery = RoleTable.to[Seq]
      if (isParamDefined(name)) commonQuery = commonQuery.filter(_.name.toUpperCase like "%" + replaceWildCard(name.get.toUpperCase) + "%")
      if (isParamDefined(selectedPrivIdArr)) {
        val subQuery = RolePrivLstTable.filter(_.privId inSet selectedPrivIdArr.get).map(_.roleId)
        commonQuery = commonQuery.filter(_.roleId in subQuery)
      }

      var pagingQuery = commonQuery.sortBy(sIdx match {
        case Some("name") => if (sOrder.getOrElse("").equals(ORDER_DESC)) _.name.desc else _.name.asc
        case Some("privName") => _.roleId.desc
        case _ => _.roleId.desc
      })

      if (isParamDefined(offset) && isParamDefined(limit)) {
        pagingQuery = pagingQuery.drop(offset.get).take(limit.get)
      }

      (commonQuery.length.result, pagingQuery.result) // paging을 위한 total rows를 위한 query, page 처리 된 result
    }

    // Display 관련 Json Format 정의
    implicit val privFormat: Format[Priv] = Json.format[Priv]
    case class DisplayRole(name: String,
                           roleId: Long,
                           priv: Seq[Priv]
                          ) extends RoleBase
    implicit val displayRoleFormat: Format[DisplayRole] = Json.format[DisplayRole]

    def getListByCondition(limit: Option[Int], offset: Option[Int], sIdx: Option[String], sOrder: Option[String], name: Option[String], privId : Option[String]) =
      SecureApiAction { implicit request =>
        val selectedPrivIdArr = if (privId.isDefined) Some(privId.get.split(DEFAULT_TIME_COLON).map(_.toLong).toSeq) else None
        val (totalRowQuery, rowsQuery) = selectByCondition(limit, offset, sIdx, sOrder, name, selectedPrivIdArr)
        val query = for {
          rows: Seq[Role] <- rowsQuery
          totalRows <- totalRowQuery
          privRowsByRoleId <- (for {
            rolePrivLstTable <- RolePrivLstTable.filter(_.roleId inSet rows.map(_.roleId))
            privTable <- PrivTable if privTable.privId === rolePrivLstTable.privId
          } yield (rolePrivLstTable.roleId, privTable)).result
        } yield (rows, totalRows, privRowsByRoleId)

        lemsdb.run(query).flatMap {
          case (rows: Seq[Role], totalRows: Int, privRowsByRoleId: Seq[(Long, Priv)]) => {
            okPaging(rows.map(role => DisplayRole(role.name, role.roleId, getLstValues[Priv](role.roleId, privRowsByRoleId))),totalRows, limit)
          }
        }
      }
*/

    override def getList(limit: Option[Int], offset: Option[Int], sIdx: Option[String], sOrder: Option[String], name: Option[String], privId: Option[Long]): Future[(Seq[Role], Seq[(Long, Priv)], Int)] = {
      var commonQuery = RoleTable.to[Seq]

      if (name.isDefined) commonQuery = commonQuery.filter { case role => role.name.toUpperCase like "%" + name.get.toUpperCase + "%" }
      if (privId.isDefined) {
        val roleIdsInSubQuery = RolePrivLstTable.filter(_.privId inSet Array(privId.get)).map(_.roleId)
        commonQuery = commonQuery.filter(_.roleId in roleIdsInSubQuery)
      }

      var pagingQuery = commonQuery.sortBy {
        case role => sIdx match {
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
        privRowsByRoleId <- (for {
          rolePrivLstTable <- RolePrivLstTable.filter(_.roleId inSet rows.map(_.roleId))
          privTable <- PrivTable if privTable.privId === rolePrivLstTable.privId
        } yield (rolePrivLstTable.roleId, privTable)).result
      } yield (rows, privRowsByRoleId, totalRows)

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