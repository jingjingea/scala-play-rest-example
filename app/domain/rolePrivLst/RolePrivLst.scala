package domain.rolePrivLst

import slick.driver.PostgresDriver.api._

import domain.priv._
import domain.role._

case class RolePrivLst(roleId: Long,
                       privId: Long)

// ROLE
class RolePrivLstTable(tag: Tag) extends Table[RolePrivLst](tag, "roleprivlst") {
  def roleId = column[Long]("roleid")
  def privId = column[Long]("privid")

  def ukPrivId = index("role_priv_lst_uk", (roleId, privId), unique = true)

  def roleId_fk = foreignKey("roleid_fk", roleId, RoleTable)(_.roleId, onDelete=ForeignKeyAction.Cascade)
  def privId_fk = foreignKey("privid_fk", privId, PrivTable)(_.privId)

  // Every table needs a * projection with the same type as the table's type parameter
  override def * = (roleId, privId) <> (RolePrivLst.tupled, RolePrivLst.unapply)
}

object RolePrivLstTable extends TableQuery(new RolePrivLstTable(_)) {

}
