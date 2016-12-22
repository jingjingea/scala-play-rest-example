package controllers

import controllers.user.UserInfoController
import domain.user.UserInfoTable
import play.api.mvc.Action
import services.userInfo.UserInfoServiceComponentImpl
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import mydb.MyDatabase._

import scala.concurrent.Future

object UserInfoApplication extends UserInfoController with UserInfoServiceComponentImpl {
    // Controller has only Action interface

    def index = Action {
       Ok("user info application index")
    }

    def createSchema = Action {
        mydb.MyDatabase.createSchema()
        Ok("create schema")
    }

    def dropSchema = Action {
        mydb.MyDatabase.dropSchema()
        Ok("drop schema")
    }
    def test = Action.async { implicit request =>
        lemsdb.run(UserInfoTable.map(user => (user.name, user.passwd, user.realName, user.authKey)) += ("test", "1234", "realName", "authKey")).flatMap{ result =>
            Future(Ok("insert user info data"))
        }
    }

}