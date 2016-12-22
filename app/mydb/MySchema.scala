package mydb

import domain.user.UserInfoTable
import org.slf4j.{Logger, LoggerFactory}
import slick.driver.PostgresDriver.api._

/**
  * Created by hana on 2016-12-21.
  */
object MySchema {
  implicit val log: Logger = LoggerFactory.getLogger(getClass)
  val schemas =
    List(
      UserInfoTable
    )

  log.info(s"${UserInfoTable.schema.createStatements}")
  schemas.map(_.schema.createStatements).foreach(_.foreach(str => log.info(str)))
  val createTable = DBIO.seq(schemas.map(table => table.schema.create): _*)

  val drop = DBIO.seq(
    schemas.map(table => sqlu"drop table if exists #${table.baseTableRow.tableName} cascade"): _*
  )
}
