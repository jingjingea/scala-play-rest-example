package controllers.user

import play.api.mvc.Controller

trait UserInfoController extends Controller {

}

case class UserInfoResource(email: String)
