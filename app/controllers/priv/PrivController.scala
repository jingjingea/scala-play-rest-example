package controllers.priv

import play.api.libs.json._
import domain.priv.Priv
import play.api.mvc.{Action, Controller}
import services.priv.PrivServiceComponent
import play.api.libs.functional.syntax._

import scala.concurrent.ExecutionContext.Implicits.global
import org.slf4j.{Logger, LoggerFactory}
import services.CommonMethods

trait PrivController extends Controller {
  self: PrivServiceComponent =>
  implicit val log: Logger = LoggerFactory.getLogger(getClass)

  def getPrivList = Action.async { request =>
    val limit: Option[Int] = request.getQueryString("limit").map(l => l.toInt)
    val offset: Option[Int] = request.getQueryString("offset").map(o => o.toInt)
    val sIdx: Option[String] = request.getQueryString("sIdx")
    val sOrder: Option[String] = request.getQueryString("sOrder")
    val name: Option[String] = request.getQueryString("name")

    privService.getList(limit, offset, sIdx, sOrder, name).map { m =>
      val privList = m._1
      val totalRows = m._2
      Ok(Json.obj(
        "rows" -> Json.arr(privList.map { priv => {
          Json.obj(
            "privId" -> priv.privId,
            "key" -> priv.key,
            "name" -> priv.name
          )
        }
        }),
        "totalPage" -> CommonMethods.getTotalPages(totalRows, limit),
        "totalRecords" -> totalRows
      )
      )
    }
  }

  def createPriv = Action(parse.json) { request =>
    val privJson = request.body
    val priv = privJson.as[Priv]

    try {
      val result = privService.createPriv(priv)
      Ok(s"created priv $result")
    } catch {
      case e: IllegalArgumentException =>
        BadRequest("Priv Not Found")
    }
  }

  def updatePriv(id: Long) = Action(parse.json) { request =>
    val privJson = request.body
    println(privJson)
    val priv = privJson.as[Priv]
    println(priv)

    try {
      val result = privService.updatePriv(id, priv)
      Ok(s"updated priv $result")
    } catch {
      case e: IllegalArgumentException =>
        BadRequest("Priv Not Found")
    }
  }

  def deletePriv(id: Long) = Action { request =>
    try {
      privService.deletePriv(id)
      Ok("delete priv id " + id)
    } catch {
      case e: IllegalArgumentException =>
        BadRequest("Priv Not Found")
    }
  }

  implicit def privReads: Reads[Priv] = (
    (__ \ "name").read[String] and
      (__ \ "key").read[Long] and
      (__ \ "privId").read[Long]
    ) (Priv.apply _)

  implicit def privWrites: Writes[Priv] = (
    (__ \ "name").write[String] and
      (__ \ "key").write[Long] and
      (__ \ "privId").write[Long]
    ) (unlift(Priv.unapply))


}