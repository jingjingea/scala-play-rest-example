package services.userInfo

import domain.user.{UserInfo, UserInfoTable}
import play.api.libs.json.JsValue
import slick.driver.PostgresDriver.api._
import mydb.MyDatabase._

trait UserInfoServiceComponent {
    val userInfoService: UserInfoService // object for implementing service components
    trait UserInfoService { // must be implemented to class in child class or trait
        // def createUserInfo(userInfo: JsValue): Unit
        def updateUserInfo(userInfo: UserInfo)
        def tryFindById(id: Long): Option[UserInfo]
        // def tryFindByEmail(email: String): Option[UserInfo]
        def delete(id: Long)
    }
}

trait UserInfoServiceComponentImpl extends UserInfoServiceComponent {
    override val userInfoService = new UserInfoServiceImpl

    class UserInfoServiceImpl extends UserInfoService {
        /*
        override def createUserInfo(userInfo: JsValue): Unit = {
            // create database
        }
        */

        override def updateUserInfo(userInfo: UserInfo): Unit = {

        }

        override def tryFindById(id: Long): Option[UserInfo] = {
            lemsdb.run(UserInfoTable.filter(_.name === "1"))
            Option(UserInfo("hanseung", "1234", "real hanseung", Some("hanseung@naver.com"), Some("010-1234-5678"), "1a0k2s9j3d8h4f7h5g6"))
        }


        override def delete(id: Long) = {

        }

    }
}