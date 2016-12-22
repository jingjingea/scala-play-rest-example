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
    val createTable = {
        println("######### create ############")
        schemas.map(table => table.schema).foreach(x => println(x))
        schemas.map(table => table.schema.create).foreach(x => println(x))
        DBIO.seq(schemas.map(table => table.schema.create): _*)
    }

    val drop = {
        println("#### drop #################")
        println(schemas.head)
        schemas.map(table => println(sqlu"drop table if exists #${table.baseTableRow.tableName} cascade"))
        DBIO.seq(schemas.map(table => sqlu"drop table if exists #${table.baseTableRow.tableName} cascade"): _*)
    }
}
