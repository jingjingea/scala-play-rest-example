package services.role

import domain.role._
import domain.rolePrivLst._
import mydb.MyDatabase._
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by user on 2016-12-26.
  */
trait RoleServiceComponent {
  val roleService: RoleService

  trait RoleService {
    def createRole(role: Role, privIdList: Seq[Long]): Unit

    def updateRole(id: Long, role: Role)

    def deleteRole(id: Long)
  }

}

trait RoleServiceComponentImpl extends RoleServiceComponent {
  override val roleService = new RoleServiceImpl

  class RoleServiceImpl extends RoleService {
    override def createRole(role: Role, privIdList: Seq[Long]): Unit = {
      val createRolePrivQueryList = for {
        newRoleId <- RoleTable returning RoleTable.map(_.roleId) += role
        _ <- RolePrivLstTable ++= privIdList.map((privId: Long) => RolePrivLst(newRoleId, privId))
      } yield newRoleId

      lemsdb.run(createRolePrivQueryList.transactionally)
    }

    override def updateRole(id: Long, role: Role) = {
      lemsdb.run(RoleTable.filter(_.roleId === id).update(role))
    }

    override def deleteRole(id: Long) = {
      lemsdb.run(RoleTable.filter(_.roleId === id).delete)
    }
  }

}
