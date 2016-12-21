package mydb

import domain.user.UserInfoTable
import play.api.Logger
import slick.driver.PostgresDriver.api._

/**
  * Created by hana on 2016-12-21.
  */
object MySchema {
  val schemas =
    List(
      UserInfoTable
    )

  Logger.info(s"${UserInfoTable.schema.createStatements}")
  schemas.map(_.schema.createStatements).foreach(_.foreach(str => Logger.info(str)))
  val createTable = DBIO.seq(schemas.map(table => table.schema.create): _*)

  val drop = DBIO.seq(
    schemas.map(table => sqlu"drop table if exists #${table.baseTableRow.tableName} cascade"): _*
  )
}
