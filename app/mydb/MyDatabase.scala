package mydb

import com.typesafe.config.ConfigFactory
import play.api.Logger
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global

/**
  * Created by hana on 2016-12-21.
  */
object MyDatabase {
  private var dbConfFileName = "db.conf"
  private val classLoader = getClass.getClassLoader
  private lazy val config = ConfigFactory.parseResources(classLoader, dbConfFileName)
  implicit lazy val lemsdb = Database.forConfig("database", config = config)

  def open() = {
    dbConfFileName = if (classLoader.getResource("db-dev.conf") != null) "db-dev.conf" else "db.conf"
    Logger.info(s"db configuration file = ${dbConfFileName}")

    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
      override def run(): Unit = {
        lemsdb.close()
      }
    }))
  }

  def close() = {
  }

  def createSchema(): Unit = {
    Logger.info("#########################")
    Logger.info("## call createStatements.")
    val createTableFuture = lemsdb.run(MySchema.createTable.transactionally)
    createTableFuture.onSuccess {case s => Logger.info(s"DB Schema Create Success: $s") }
    createTableFuture.onFailure { case e => Logger.warn(s"DB Schema Create Failure: $e") }
  }

  def dropSchema(): Unit = {
    Logger.info("#########################")
    Logger.info("## call dropSchema.")
    val future = lemsdb.run(MySchema.drop.transactionally)
    future.onSuccess { case s => Logger.info(s"DB Schema Drop Success: $s") }
    future.onFailure { case e => Logger.warn(s"DB Schema Drop Failure: $e ") }
  }
}
