package services.role

import domain.role._
import mydb.MyDatabase._
import slick.driver.PostgresDriver.api._

import scala.concurrent.Future

/**
  * Created by user on 2016-12-26.
  */
trait RoleServiceComponent {
  val roleService: RoleService

  trait RoleService {
    def createRole(role: Role): Future[Int]
  }

}

trait RoleServiceComponentImpl extends RoleServiceComponent {
  override val roleService = new RoleServiceImpl

  class RoleServiceImpl extends RoleService {
    override def createRole(role: Role): Future[Int] = {
      lemsdb.run(RoleTable += role)
    }

  }
}
