package domain.user

import slick.driver.PostgresDriver.api._
import domain.role._

case class UserInfo(name: String,
                passwd: String,
                realName: String,
                email: Option[String],
                tel: Option[String],
                roleId: Long,
                authKey: String,
                userInfoId: Long = 0L
               )

// USERINFO
class UserInfoTable(tag: Tag) extends Table[UserInfo](tag, "userinfo") {
  def name = column[String]("name", O.Length(30))
  def passwd = column[String]("passwd")
  def realName = column[String]("realname", O.Length(30))
  def email = column[Option[String]]("email", O.Length(50))
  def tel = column[Option[String]]("tel" , O.Length(15))
  def roleId = column[Long]("roleid")
  def authKey = column[String]("authkey")
  def userInfoId = column[Long]("userinfoid", O.PrimaryKey, O.AutoInc)

  def roleId_fk = foreignKey("roleid_fk", roleId, RoleTable)(_.roleId, onDelete=ForeignKeyAction.Cascade)

  def ukName = index("userinfo_name_uk", name, unique = true)

  // Every table needs a * projection with the same type as the table's type parameter
  override def * = (name, passwd, realName, email, tel, roleId, authKey, userInfoId) <> (UserInfo.tupled, UserInfo.unapply)
}

object UserInfoTable extends TableQuery(new UserInfoTable(_)) {

}
