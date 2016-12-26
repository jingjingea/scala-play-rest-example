package mydb

import com.typesafe.config.ConfigFactory
import org.slf4j.{Logger, LoggerFactory}
import slick.driver.PostgresDriver.api._

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

/**
  * Created by hana on 2016-12-21.
  */
object MyDatabase {
  implicit val log: Logger = LoggerFactory.getLogger(getClass)
  private var dbConfFileName = "db.conf"
  private val classLoader = getClass.getClassLoader
  private lazy val config = ConfigFactory.parseResources(classLoader, dbConfFileName)
  implicit lazy val lemsdb = Database.forConfig("database", config = config)

  def open() = {
    dbConfFileName = if (classLoader.getResource("db-dev.conf") != null) "db-dev.conf" else "db.conf"
    log.info(s"db configuration file = ${dbConfFileName}")

    Runtime.getRuntime.addShutdownHook(new Thread(new Runnable {
      override def run(): Unit = {
        lemsdb.close()
      }
    }))
  }

  def close() = {
    log.info("## close Application...")
    lemsdb.close()
  }

  def createSchema(): Unit = {
    log.info("## call createStatements.")
    val createTableFuture: Future[Unit] = lemsdb.run(MySchema.createTable.transactionally)
    createTableFuture.onSuccess { case s => log.info(s"DB Schema Create Success: $s") }
    createTableFuture.onFailure { case e => log.warn(s"DB Schema Create Failure: $e") }
  }

  def dropSchema(): Unit = {
    log.info("## call dropSchema.")
    val future: Future[Unit] = lemsdb.run(MySchema.drop.transactionally)
    future.onSuccess { case s => log.info(s"DB Schema Drop Success: $s") }
    future.onFailure { case e => log.warn(s"DB Schema Drop Failure: $e ") }
  }
}
