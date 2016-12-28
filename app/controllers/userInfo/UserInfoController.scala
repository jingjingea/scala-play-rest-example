package controllers.user

import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import services.userInfo.UserInfoServiceComponent
import domain.user.UserInfo
import play.api.libs.functional.syntax._
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.{Future, Await}
import scala.concurrent.duration.Duration
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

trait UserInfoController extends Controller {
  self: UserInfoServiceComponent =>

  implicit val log: Logger = LoggerFactory.getLogger(getClass)

  def createUserInfo = Action(parse.json) { request =>
    println("create user info")
    val userInfoJson = request.body
    val userInfo = userInfoJson.as[UserInfo]
    try {
      log.info(s"$userInfo")
      val result = userInfoService.createUserInfo(userInfo)
      Ok("create user info record" + result)
    } catch {
      case e: IllegalArgumentException =>
        BadRequest("User Info Not Found")
    }
  }

  def updateUserInfo(id: Long) = Action(parse.json) { request =>
    val userInfoJson = request.body
    val userInfo = userInfoJson.as[UserInfo]
    try {
      log.info(s"$userInfo")
      val result = userInfoService.updateUserInfo(id, userInfo)
        Ok("update user info record: " + result)
    } catch {
      case e: IllegalArgumentException =>
      BadRequest("User Info Not Found")
    }
  }

  def deleteUserInfo(id: Long) = Action { request =>
    try {
      userInfoService.delete(id)
      Ok("delete user info id " + id)
    } catch {
      case e: IllegalArgumentException =>
      BadRequest("User Info Not Found")
    }
  }

  def findUserInfoById(id: Long) = Action.async {
    userInfoService.tryFindById(id).map { m =>
      Ok(Json.toJson(m))
    }
  }

  implicit def anyReads: Reads[Any] = {
    (__ \ "name").read[String] and
      (__ \ "privId").read[Long] and
      (__ \ "key").read[Long] and
      (__ \ "name").read[String]
  }

  implicit def userInfoReads: Reads[UserInfo] = (
    (__ \ "name").read[String] and
      (__ \ "passwd").read[String] and
      (__ \ "realName").read[String] and
      (__ \ "email").readNullable[String] and
      (__ \ "tel").readNullable[String] and
      (__ \ "roleId").read[Long] and
      (__ \ "authKey").read[String] and
      (__ \ "userInfoId").read[Long]
    )(UserInfo.apply _)

  implicit def userInfoWrites: Writes[UserInfo] = (
    (__ \ "name").write[String] and
      (__ \ "passwd").write[String] and
      (__ \ "realName").write[String] and
      (__ \ "email").writeNullable[String] and
      (__ \ "tel").writeNullable[String] and
      (__ \ "roleId").write[Long] and
      (__ \ "authKey").write[String] and
      (__ \ "userInfoId").write[Long]
    )(unlift(UserInfo.unapply))

}

case class UserInfoResource(email: String)
