package services.userInfo

import domain.user.UserInfo

trait UserInfoServiceComponent {
    val userInfoService: UserInfoService // object for implementing service components
    trait UserInfoService { // must be implemented to class in child class or trait
        // def createUserInfo(userInfo: UserInfo): UserInfo
        def updateUserInfo(userInfo: UserInfo)
        // def tryFindById(id: Long): Option[UserInfo]
        // def tryFindByEmail(email: String): Option[UserInfo]
        def delete(id: Long)
    }
}

trait UserInfoServiceComponentImpl extends UserInfoServiceComponent {
    override val userInfoService: UserInfoService = new UserInfoServiceImpl

    class UserInfoServiceImpl extends UserInfoService {
        /*
        override def createUserInfo(userInfo: UserInfo): UserInfo = {
            // create database
        }
        */

        override def updateUserInfo(userInfo: UserInfo): Unit = {

        }

        override def delete(id: Long) = {

        }

    }
}