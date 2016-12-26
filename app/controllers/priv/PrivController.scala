package controllers.priv

import play.api.libs.json._
import domain.priv.Priv
import play.api.mvc.{Action, Controller}
import services.priv.PrivServiceComponent
import play.api.libs.functional.syntax._

import scala.concurrent.ExecutionContext.Implicits.global
import org.slf4j.{Logger, LoggerFactory}

import scala.concurrent.Future

trait PrivController extends Controller {
  self: PrivServiceComponent =>
  implicit val log: Logger = LoggerFactory.getLogger(getClass)

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

  implicit def privReads: Reads[Priv] = (
    (__ \ "name").read[String] and
      (__ \ "key").read[Long] and
      (__ \ "privId").read[Long]
    )(Priv.apply _)

  implicit def privWrites: Writes[Priv] = (
    (__ \ "name").write[String] and
      (__ \ "key").write[Long] and
      (__ \ "privId").write[Long]
    )(unlift(Priv.unapply))


}