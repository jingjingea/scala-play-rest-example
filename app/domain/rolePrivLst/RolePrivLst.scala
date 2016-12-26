package domain.rolePrivLst

import slick.driver.PostgresDriver.api._

import domain.priv._
import domain.role._

case class RolePrivLst(roidId: Long,
                       privId: Long)

// ROLE
class RolePrivLstTable(tag: Tag) extends Table[RolePrivLst](tag, "roleprivlst") {
  def privId = column[Long]("privid")
  def roleId = column[Long]("roleid")

  def ukPrivId = index("role_priv_lst_uk", (roleId, privId), unique = true)

  def privId_fk = foreignKey("privid_fk", privId, PrivTable)(_.privId)
  def roleId_fk = foreignKey("roleid_fk", roleId, RoleTable)(_.roleId)

  // Every table needs a * projection with the same type as the table's type parameter
  override def * = (privId, roleId) <> (RolePrivLst.tupled, RolePrivLst.unapply)
}

object RolePrivLstTable extends TableQuery(new RolePrivLstTable(_)) {

}
