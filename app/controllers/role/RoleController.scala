package controllers.role

import play.api.libs.json._
import play.api.mvc.{Action, Controller}
import services.role.RoleServiceComponent
import domain.role.Role
import play.api.libs.functional.syntax._

trait RoleController extends Controller {
  self: RoleServiceComponent =>

  def createRole = Action(parse.json) { request =>
    val roleJson = request.body
    val role = roleJson.as[Role]

    try {
      val result = roleService.createRole(role)
      Ok(s"create role : $result")
    } catch {
      case e: IllegalArgumentException =>
        BadRequest("Role Not Found")
    }
  }

  implicit def roleReads: Reads[Role] = (
    (__ \ "name").read[String] and
      (__ \ "roleId").read[Long]
    )(Role.apply _)

  implicit def roleWrites: Writes[Role] = (
    (__ \ "name").write[String] and
      (__ \ "roleId").write[Long]
    )(unlift(Role.unapply))

}

