package services.userInfo

import domain.user.{UserInfo, UserInfoTable}
import mydb.MyDatabase._
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration
import scala.concurrent.{Await, Future}
import scala.util.{Failure, Success}

trait UserInfoServiceComponent {
  val userInfoService: UserInfoService

  // object for implementing service components
  trait UserInfoService {
    // must be implemented to class in child class or trait
    def getList

    def createUserInfo(userInfo: UserInfo) : Future[Int]

    def updateUserInfo(id: Long, userInfo: UserInfo)

    def tryFindById(id: Long): Future[Option[UserInfo]]

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

    override def tryFindById(id: Long): Future[Option[UserInfo]] = {
      // val test: Future[Option[UserInfo]] = lemsdb.run(UserInfoTable.filter(_.userInfoId === id).result.headOption)
      // val test1: Future[Seq[UserInfo]] = lemsdb.run(UserInfoTable.filter(_.userInfoId === id).result)
      val result: Future[Option[UserInfo]] = lemsdb.run(UserInfoTable.filter(_.userInfoId === id).result.headOption)
      return result
    }

    override def delete(id: Long) = {
      lemsdb.run(UserInfoTable.filter(_.userInfoId === id).delete)
    }
  }

}
