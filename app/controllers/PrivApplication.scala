package controllers

import controllers.priv.PrivController
import services.priv.PrivServiceComponentImpl

object PrivApplication extends PrivController with PrivServiceComponentImpl {

}