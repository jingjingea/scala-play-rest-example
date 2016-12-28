package controllers.user

import play.api.libs.json._
import play.api.mvc.{Action, AnyContent, Controller}
import services.userInfo.UserInfoServiceComponent
import domain.user.UserInfo
import play.api.libs.functional.syntax._
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.ExecutionContext.Implicits.global

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

  def findUserInfoById(id: Long): Action[AnyContent] = Action.async {
    userInfoService.tryFindById(id).map { m => {
      val user = m._1
      val role = m._2
      val priv = m._3
      Ok(Json.obj(
        "name" -> user.name,
        "passwd" -> user.passwd,
        "realName" -> user.realName,
        "email" -> user.email,
        "tel" -> user.tel,
        "roleId" -> user.roleId,
        "roleName" -> role.name,
        "authKey" -> user.authKey,
        "userInfoId" -> user.userInfoId,
        "priv" -> Json.arr(
          priv.map { p =>
            Json.obj(
              "privId" -> p.privId,
              "key" -> p.key,
              "name" -> p.name
            )
          }
        )
      ))
    }
    }
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
    ) (UserInfo.apply _)

  implicit def userInfoWrites: Writes[UserInfo] = (
    (__ \ "name").write[String] and
      (__ \ "passwd").write[String] and
      (__ \ "realName").write[String] and
      (__ \ "email").writeNullable[String] and
      (__ \ "tel").writeNullable[String] and
      (__ \ "roleId").write[Long] and
      (__ \ "authKey").write[String] and
      (__ \ "userInfoId").write[Long]
    ) (unlift(UserInfo.unapply))

}

case class UserInfoResource(email: String)
