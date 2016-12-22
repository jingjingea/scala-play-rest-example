package controllers

import controllers.user.UserController
import domain.user.{UserInfo, UserInfoTable}
import play.api.mvc._
import services.user.UserServiceComponentImpl
import repositories.user.UserRepositoryComponentImpl
import slick.driver.PostgresDriver.api._
import play.api.libs.functional.syntax._

import scala.concurrent.ExecutionContext.Implicits.global
import mydb.MyDatabase._

import scala.concurrent.Future


object Application extends UserController
                   with UserServiceComponentImpl
                   with UserRepositoryComponentImpl {

  def index = Action {
    println("git test")
    Ok("test")
  }

  def createSchema = Action {
    mydb.MyDatabase.createSchema()
    Ok("create schema")
  }

  def dropSchema = Action {
    mydb.MyDatabase.dropSchema()
    Ok("drop schema")
  }

  def test =  Action.async { implicit request =>
    lemsdb.run(UserInfoTable.map(user => (user.name, user.passwd, user.realName, user.authKey)) += ("test", "1234", "trealName", "authKey")).flatMap{ result =>
      Future(Ok("insert data"))
    }
  }
}