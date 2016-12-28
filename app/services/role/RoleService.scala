package services.role

import domain.role._
import domain.rolePrivLst._
import mydb.MyDatabase._
import slick.dbio.Effect.Write
import slick.driver.PostgresDriver.api._
import slick.profile.FixedSqlAction

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by user on 2016-12-26.
  */
trait RoleServiceComponent {
  val roleService: RoleService
  trait RoleService {
    def createRole(role: Role, privIdList: Seq[Long]): Unit
  }
}

trait RoleServiceComponentImpl extends RoleServiceComponent {
  override val roleService = new RoleServiceImpl

  class RoleServiceImpl extends RoleService {
    override def createRole(role: Role, privIdList: Seq[Long]): Unit = {
      println(privIdList)
      val createRolePrivQueryList = for {
        newRoleId: Long <- RoleTable returning RoleTable.map(_.roleId) += role
        _ <- RolePrivLstTable ++= privIdList.map((privId: Long) => RolePrivLst(newRoleId, privId))
      } yield newRoleId

      lemsdb.run(createRolePrivQueryList.transactionally)
    }

  }
}
