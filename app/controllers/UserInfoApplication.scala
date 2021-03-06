package controllers

import controllers.user.UserInfoController
import domain.user.{UserInfo, UserInfoTable}
import mydb.MyDatabase._
import play.api.mvc.Action
import services.userInfo.UserInfoServiceComponentImpl
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

// controller(실질적 Action interface 역할을 하는)는 어디서든 호출이 가능하도록 object로 선언
class UserInfoApplication extends UserInfoController with UserInfoServiceComponentImpl {
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

  /*
  def test(num: Int) = Action.async { implicit request =>
      val test: UserInfo = UserInfo("name", "1234", "test", Some("jingjingea@nate.com"), Some("010-1231234"), 1, "auth")
      userInfoService.createUserInfo(test) map { (createdCnt: Int) =>
          Ok(s"$createdCnt 개 add")
      }
  }

  def test1() = Action.async { implicit request =>
    // test
      val test1 = UserInfoTable += UserInfo("name1", "1234", "test", Some("jingjingea@nate.com"), Some("010-1231234"), 1, "auth")
      val test2 = UserInfoTable += UserInfo("name2", "1234", "test", Some("jingjingea@nate.com"), Some("010-1231234"), 1, "auth")
      val test3 = UserInfoTable += UserInfo("name3", "1234", "test", Some("jingjingea@nate.com"), Some("010-1231234"), 1, "auth")
      lemsdb.run(DBIO.seq(test1, test2, test3)) map { createdCnt =>
          Ok(s"$createdCnt 개 add")
      }
  }
  */
}