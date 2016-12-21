package domain.user

import slick.driver.PostgresDriver.api._

case class UserInfo(name: String,
                passwd: String,
                realName: String,
                email: Option[String],
                tel: Option[String],
                authKey: String,
                UserInfoId: Long = 0L
               )

// USERINFO
class UserInfoTable(tag: Tag) extends Table[UserInfo](tag, "userinfo") {
  def name = column[String]("name", O.Length(30))
  def passwd = column[String]("passwd")
  def realName = column[String]("realname", O.Length(30))
  def email = column[Option[String]]("email", O.Length(50))
  def tel = column[Option[String]]("tel" , O.Length(15))
  def authKey = column[String]("authkey")
  def userInfoId = column[Long]("userinfoid", O.PrimaryKey, O.AutoInc)

  def ukName = index("userInfo_name_uk", name, unique = true)

  // Every table needs a * projection with the same type as the table's type parameter
  def * = (name, passwd, realName, email, tel, authKey, userInfoId) <> (UserInfo.tupled, UserInfo.unapply)
}

object UserInfoTable extends TableQuery(new UserInfoTable(_)) {

}
