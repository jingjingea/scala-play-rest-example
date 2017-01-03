package controllers.role

import services.CommonMethods
import domain.priv.Priv
import play.api.libs.json._
import play.api.libs.json.Json
import play.api.mvc.{Action, Controller}
import services.role.RoleServiceComponent
import domain.role.Role
import play.api.libs.functional.syntax._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

trait RoleController extends Controller {
  self: RoleServiceComponent =>

  def getRoleList = Action.async { request =>
    val limit: Option[Int] = request.getQueryString("limit").map(l => l.toInt)
    val offset: Option[Int] = request.getQueryString("offset").map(o => o.toInt)
    val sIdx: Option[String] = request.getQueryString("sIdx")
    val sOrder: Option[String] = request.getQueryString("sOrder")
    val name: Option[String] = request.getQueryString("name")
    val privId = request.getQueryString("privId").map(l => l.toLong)

    roleService.getList(limit, offset, sIdx, sOrder, name, privId).map { m =>
      val roleList: Seq[Role] = m._1
      val privList: Seq[(Long, Priv)] = m._2
      val totalRows: Int = m._3

      Ok(Json.obj(
        "rows" ->
          roleList.map(role => {
            Json.obj(
              "name" -> role.name,
              "roleId" -> role.roleId,
              "priv" -> privList.filter(_._1 == role.roleId).map {
                case (_, priv) =>
                  Json.obj(
                    "name" -> priv.name,
                    "key" -> priv.key,
                    "privId" -> priv.privId
                  )
              }
            )
          }
          ),
        "totalPage" -> CommonMethods.getTotalPages(totalRows, limit),
        "totalRecordes" -> totalRows
      ))
    }
  }

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
    ) (Role.apply _)

  implicit def roleWrites: Writes[Role] = (
    (__ \ "name").write[String] and
      (__ \ "roleId").write[Long]
    ) (unlift(Role.unapply))
}

