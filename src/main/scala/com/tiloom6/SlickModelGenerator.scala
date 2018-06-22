package com.tiloom6

import com.typesafe.config.ConfigFactory
import slick.codegen.SourceCodeGenerator
import slick.jdbc.JdbcBackend._
import slick.{model => m}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object SlickModelGenerator extends App {

  private val config = ConfigFactory.load("wannamysql")
  private val db = Database.forConfig("mysql.db", config = config)
  private val driver = slick.jdbc.MySQLProfile

  // TODO なんかうまくいかない
  val model = Await.result( db.run( driver.createModel().withPinnedSession ), Duration.Inf )
  println(model.tablesByName)

  CustomSourceCodeGenerator( model ).writeToFile( "slick.jdbc.MySQLProfile", "src/main/scala", "com.tiloom6" )

}

case class CustomSourceCodeGenerator( model: m.Model ) extends SourceCodeGenerator( model ) {

  override def code = "import com.github.tototoshi.slick.MySQLJodaSupport._\n" + "import org.joda.time.DateTime\n" + super.code

  override def Table = new Table( _ ) {
    override def Column = new Column( _ ) {
      override def rawType = model.tpe match {
        case "java.sql.Timestamp" => "DateTime"
        case _ => super.rawType
      }
    }
  }
}