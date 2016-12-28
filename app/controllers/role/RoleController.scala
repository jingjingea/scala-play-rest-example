package controllers.role

import play.api.libs.json._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.role.RoleServiceComponent
import domain.role.Role
import play.api.libs.functional.syntax._

trait RoleController extends Controller {
  self: RoleServiceComponent =>

  def createRole = Action(parse.json) { request =>
    val roleJson: JsValue = request.body

    val privIdList: Seq[Long] = (roleJson \ "privId").as[Seq[Long]]
    val role = roleJson.as[Role]

    try {
      val result = roleService.createRole(role, privIdList)
      Ok(s"created role id : $result")
    } catch {
      case e: IllegalArgumentException =>
        BadRequest("Role Not Found")
    }
  }

  def updateRole(id: Long) = Action(parse.json) { request =>
    val roleJson = request.body

    // val privIdList: Seq[Long] = (roleJson \ "privId").as[Seq[Long]] // ?
    val role = roleJson.as[Role]

    try {
      val result = roleService.updateRole(id, role)
      Ok(s"updated role : $result")
    } catch {
      case e: IllegalArgumentException =>
        BadRequest("Role Not Found")
    }
  }
  def deleteRole(id: Long) = Action { request =>
    try {
      println(s"################################################################## $id")
      roleService.deleteRole(id)
      Ok("delete role " + id)
    } catch {
      case e: IllegalArgumentException =>
        BadRequest("Role Not Found")
    }
  }

  // implicit val roleFormatter = Json.format[Role]

  implicit def roleReads: Reads[Role] = (
    (__ \ "name").read[String] and
      (__ \ "roleId").read[Long]
    )(Role.apply _)

  implicit def roleWrites: Writes[Role] = (
    (__ \ "name").write[String] and
      (__ \ "roleId").write[Long]
    )(unlift(Role.unapply))
}

