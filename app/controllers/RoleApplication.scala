package controllers

import controllers.role.RoleController
import services.role.RoleServiceComponentImpl

object RoleApplication extends RoleController with RoleServiceComponentImpl {

}