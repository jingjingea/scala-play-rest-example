package domain.priv

import slick.driver.PostgresDriver.api._

case class Priv(name: String,
                key: Long,
                privId: Long = 0L)

class PrivTable(tag: Tag) extends Table[Priv](tag, "priv") {
  def name = column[String]("name", O.Length(30))
  def key = column[Long]("key", O.Length(50))
  def privId = column[Long]("privid", O.PrimaryKey, O.AutoInc)

  def ukKey = index("priv_key_uk", key, unique = true)

  override def * = (name, key, privId) <> (Priv.tupled, Priv.unapply)
}

object PrivTable extends TableQuery(new PrivTable(_)) {

}

