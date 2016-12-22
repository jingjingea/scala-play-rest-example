package controllers

import controllers.user.UserController
import domain.user.UserInfoTable
import mydb.MyDatabase._
import play.api.mvc._
import repositories.user.UserRepositoryComponentImpl
import services.user.UserServiceComponentImpl
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future


object Application extends UserController
  with UserServiceComponentImpl
  with UserRepositoryComponentImpl {

  def index = Action {
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

  def test = Action.async { implicit request =>
    lemsdb.run(UserInfoTable.map(user => (user.name, user.passwd, user.realName, user.authKey)) += ("test", "1234", "trealName", "authKey")).flatMap { result =>
      Future(Ok("insert data"))
    }
  }
}