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
    def getList

    def createUserInfo(userInfo: UserInfo): Future[Int]

    def updateUserInfo(id: Long, userInfo: UserInfo)

    def tryFindById(id: Long): Future[Option[Any]]

    // def tryFindByEmail(email: String): Option[UserInfo]
    def delete(id: Long)

  }

}

trait UserInfoServiceComponentImpl extends UserInfoServiceComponent {
  override val userInfoService = new UserInfoServiceImpl

  class UserInfoServiceImpl extends UserInfoService {

    override def getList = {
      val list: Future[Seq[UserInfo]] = lemsdb.run(UserInfoTable.result)
    }

    override def createUserInfo(userInfo: UserInfo): Future[Int] = {
      lemsdb.run(UserInfoTable += userInfo)
    }

    override def updateUserInfo(id: Long, userInfo: UserInfo) = {
      lemsdb.run(UserInfoTable.filter(_.userInfoId === id).update(userInfo))
    }

    override def tryFindById(id: Long): Future[(UserInfo, Role, Seq[Priv])] = {
      // val test: Future[Option[UserInfo]] = lemsdb.run(UserInfoTable.filter(_.userInfoId === id).result.headOption)
      // val test1: Future[Seq[UserInfo]] = lemsdb.run(UserInfoTable.filter(_.userInfoId === id).result)
      val result: Future[Option[UserInfo]] = lemsdb.run(UserInfoTable.filter(_.userInfoId === id).result.headOption)

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
