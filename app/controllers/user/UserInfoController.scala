package controllers.user

import domain.role.Role
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

  def getList = Action.async { request =>
    val limit: Option[Int] = request.getQueryString("limit").map(l => l.toInt)
    val offset: Option[Int] = request.getQueryString("offset").map(o => o.toInt)
    val sIdx: Option[String] = request.getQueryString("sIdx")
    val sOrder: Option[String] = request.getQueryString("sOrder")
    val name: Option[String] = request.getQueryString("name")
    val realName: Option[String] = request.getQueryString("realName")
    val roleId = request.getQueryString("roleId").map(r => r.toLong)

    userInfoService.getList(limit, offset, sIdx, sOrder, name, realName, roleId).map { m => {
      val userList: Seq[(UserInfo, Role)] = m._1
      val totalRows = m._2

      Ok(Json.obj(
        "rows" -> Json.arr(userList.map(userInfo => {
          val user = userInfo._1
          val role = userInfo._2
          Json.obj(
            "name" -> user.name,
            "passwd" -> user.passwd,
            "realName" -> user.realName,
            "tel" -> user.tel,
            "authKey" -> user.authKey,
            "roleName" -> role.name,
            "roleId" -> role.roleId,
            "userId" -> user.userInfoId
          )
        }
        )
        ),
        "totalPages" -> getTotalPages(totalRows, limit),
        "totalRecords" -> totalRows
      ))
    }
    }
  }


  def createUserInfo = Action.async(parse.json) { request =>
    val userInfoJson = request.body
    val userInfo = userInfoJson.as[UserInfo]

    userInfoService.createUserInfo(userInfo).map { rows =>
      Ok(Json.obj("rows" -> rows))
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

  def isNameExist(name: String) = Action.async { request =>
    userInfoService.countName(name).map { n =>
      if (n > 0) Ok(Json.obj("result" -> true))
      else Ok(Json.obj("result" -> false))
    }
  }

  def getTotalPages(total: Int, limit: Option[Int]) = {
    val lim = limit match {
      case Some(v) => v
      case None => total
    }
    (total % lim) match {
      case 0 => total / lim
      case _ => (total / lim) + 1
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
