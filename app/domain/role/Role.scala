package domain.role

import slick.driver.PostgresDriver.api._

case class Role(name: String,
               roleId: Long = 0L)

// ROLE
class RoleTable(tag: Tag) extends Table[Role](tag, "role") {
  def name = column[String]("name", O.Length(30))
  def roleId = column[Long]("roleid", O.PrimaryKey, O.AutoInc)

  def ukName = index("role_name_uk", name, unique = true)

  // Every table needs a * projection with the same type as the table's type parameter
  override def * = (name, roleId) <> (Role.tupled, Role.unapply)
}

object RoleTable extends TableQuery(new RoleTable(_)) {

}
