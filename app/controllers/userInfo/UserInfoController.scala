package controllers.user

import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import services.userInfo.UserInfoServiceComponent
import domain.user.UserInfo
import play.api.libs.functional.syntax._

import scala.concurrent.Future

trait UserInfoController extends Controller {
  self: UserInfoServiceComponent =>

  implicit def userInfoReads: Reads[UserInfo] = (
    (JsPath \ "name").read[String] and
      (JsPath \ "passwd").read[String] and
      (JsPath \ "realName").read[String] and
      (JsPath \ "email").readNullable[String] and
      (JsPath \ "tel").readNullable[String] and
      (JsPath \ "authKey").read[String] and
      (JsPath \ "userInfoId").read[Long]
    )(UserInfo.apply _)

  implicit def userInfoWrites: Writes[UserInfo] = (
    (JsPath \ "name").write[String] and
      (JsPath \ "passwd").write[String] and
      (JsPath \ "realName").write[String] and
      (JsPath \ "email").writeNullable[String] and
      (JsPath \ "tel").writeNullable[String] and
      (JsPath \ "authKey").write[String] and
      (JsPath \ "userInfoId").write[Long]
    )(unlift(UserInfo.unapply))

  def createUserInfo = Action(parse.json) { request =>
    println("create user info")
    val userInfoJson = request.body
    val userInfo = userInfoJson.as[UserInfo]
    try {
      val result = userInfoService.createUserInfo(userInfo)
      // save to DB
      Ok("create user info record")
    } catch {
      case e: IllegalArgumentException =>
        BadRequest("User Info Not Found")
    }
  }

  //    def findUserInfoById(id: Long) = Action {
  //        val userInfo = userInfoService.tryFindById(id)
  //        if (userInfo.isDefined) {
  //            Ok(Json.toJson(userInfo))
  //        } else {
  //            NotFound
  //        }
  //    }

}

case class UserInfoResource(email: String)
