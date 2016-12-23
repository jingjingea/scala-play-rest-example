package services.userInfo

import domain.user.{UserInfo, UserInfoTable}
import mydb.MyDatabase._
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

trait UserInfoServiceComponent {
    val userInfoService: UserInfoService

    // object for implementing service components
    trait UserInfoService {
        // must be implemented to class in child class or trait
        def createUserInfo(userInfo: UserInfo) : Future[Int]

        def updateUserInfo(userInfo: UserInfo)

        def tryFindById(id: Long)

        // def tryFindByEmail(email: String): Option[UserInfo]
        def delete(id: Long)
    }

}

trait UserInfoServiceComponentImpl extends UserInfoServiceComponent {
    override val userInfoService = new UserInfoServiceImpl

    class UserInfoServiceImpl extends UserInfoService {
        override def createUserInfo(userInfo: UserInfo) : Future[Int]= {
            lemsdb.run(UserInfoTable += userInfo)
        }

        override def updateUserInfo(userInfo: UserInfo) = {
            lemsdb.run(UserInfoTable.map(user => (user.tel, user.name)).update(userInfo.tel, userInfo.name))
        }

        override def tryFindById(id: Long) = {

            val test: Future[Option[UserInfo]] = lemsdb.run(UserInfoTable.filter(_.userInfoId === id).result.headOption)

            val test1: Future[Seq[UserInfo]] = lemsdb.run(UserInfoTable.filter(_.userInfoId === id).result)
        }

        override def delete(id: Long) = {
            lemsdb.run(UserInfoTable.filter(_.userInfoId === id).delete)
        }
    }

}