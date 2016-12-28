package services.userInfo

import domain.priv.{Priv, PrivTable}
import domain.role.{Role, RoleTable}
import domain.rolePrivLst.RolePrivLstTable
import domain.user.{UserInfo, UserInfoTable}
import mydb.MyDatabase._
import slick.dbio.DBIOAction
import slick.dbio.Effect.Read
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait UserInfoServiceComponent {
  val userInfoService: UserInfoService

  // object for implementing service components
  trait UserInfoService {
    // must be implemented to class in child class or trait
    def getList(limit: Option[Int], offset: Option[Int], sIdx: Option[String], sOrder: Option[String], name: Option[String], realName: Option[String], roleId: Option[Long]): Future[(Seq[(UserInfo, Role)], Int)]

    def createUserInfo(userInfo: UserInfo): Future[Int]

    def updateUserInfo(id: Long, userInfo: UserInfo)

    def tryFindById(id: Long): Future[(UserInfo, Role, Seq[Priv])]

    // def tryFindByEmail(email: String): Option[UserInfo]
    def delete(id: Long)

  }

}

trait UserInfoServiceComponentImpl extends UserInfoServiceComponent {
  override val userInfoService = new UserInfoServiceImpl

  class UserInfoServiceImpl extends UserInfoService {
    final val ORDER_ASC = "ASC"
    final val ORDER_DESC = "DESC"

    override def getList(limit: Option[Int], offset: Option[Int], sIdx: Option[String], sOrder: Option[String], name: Option[String], realName: Option[String], roleId: Option[Long]): Future[(Seq[(UserInfo, Role)], Int)] = {
      var commonQuery = for {
        user <- UserInfoTable
        role <- RoleTable if user.roleId === role.roleId
      } yield (user, role)


      if (name.isDefined) commonQuery = commonQuery.filter { case (ctrl, _) => ctrl.name.toUpperCase like "%" + name.get.toUpperCase + "%" }
      if (realName.isDefined) commonQuery = commonQuery.filter { case (ctrl, _) => ctrl.realName.toUpperCase like "%" + realName.get.toUpperCase + "%" }
      if (roleId.isDefined) commonQuery = commonQuery.filter { case (ctrl, _) => ctrl.roleId === roleId }

      var pagingQuery = commonQuery.sortBy { case (ctrl, _) =>
        sIdx match {
          case Some("name") => if (sOrder.getOrElse("").equals(ORDER_DESC)) ctrl.name.desc else ctrl.name.asc
          case Some("realName") => if (sOrder.getOrElse("").equals(ORDER_DESC)) ctrl.realName.desc else ctrl.realName.asc
          case Some("email") => if (sOrder.getOrElse("").equals(ORDER_DESC)) ctrl.email.desc else ctrl.email.asc
          case Some("tel") => if (sOrder.getOrElse("").equals(ORDER_DESC)) ctrl.tel.desc else ctrl.tel.asc
          case _ => ctrl.userInfoId.desc
        }
      }

      if (offset.isDefined && limit.isDefined) {
        pagingQuery = pagingQuery.drop(offset.get).take(limit.get)
      }

      val query = for {
        rows: Seq[(UserInfo, Role)] <- pagingQuery.result
        totalRows <- commonQuery.length.result
      } yield (rows, totalRows)

      lemsdb.run(query)
    }

    override def createUserInfo(userInfo: UserInfo): Future[Int] = {
      lemsdb.run(UserInfoTable += userInfo)
    }

    override def updateUserInfo(id: Long, userInfo: UserInfo): Unit = {
      lemsdb.run(UserInfoTable.filter(_.userInfoId === id).update(userInfo))
    }

    override def tryFindById(id: Long): Future[(UserInfo, Role, Seq[Priv])] = {
      // val test: Future[Option[UserInfo]] = lemsdb.run(UserInfoTable.filter(_.userInfoId === id).result.headOption)
      // val test1: Future[Seq[UserInfo]] = lemsdb.run(UserInfoTable.filter(_.userInfoId === id).result)
      // val result: Future[Option[UserInfo]] = lemsdb.run(UserInfoTable.filter(_.userInfoId === id).result.headOption)

      val query: DBIOAction[(UserInfo, Role, Seq[Priv]), NoStream, Read with Read] = for {
        (user, role) <- (
          for {user <- UserInfoTable if user.userInfoId === id
               role <- RoleTable if user.roleId === role.roleId
          } yield (user, role)).result.head
        privRows <- (for {
          rolePrivLstTable <- RolePrivLstTable.filter(_.roleId === user.roleId)
          privTable <- PrivTable if privTable.privId === rolePrivLstTable.privId
        } yield privTable).result
      } yield (user, role, privRows)

      lemsdb.run(query)
    }

    override def delete(id: Long) = {
      lemsdb.run(UserInfoTable.filter(_.userInfoId === id).delete)
    }
  }

}
