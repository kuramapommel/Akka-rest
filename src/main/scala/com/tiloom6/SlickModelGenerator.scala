package com.tiloom6

import com.typesafe.config.ConfigFactory
import slick.codegen.SourceCodeGenerator
import slick.jdbc.meta.MTable
import slick.{model => m}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

object SlickModelGenerator extends App {
  import slick.jdbc.MySQLProfile.api._

  private val config = ConfigFactory.load("wannamysql")
  private val db = Database.forConfig("mysql.db", config = config)
  private val driver = slick.jdbc.MySQLProfile

  val res = Await.result(db.run(sql"select * from wannatag".as[(Long, String, String, String, java.sql.Date)]), Duration.Inf)
  println(res)

  // tablesを指定してあげる、MySQLProfileのデフォルトはカタログの指定がないため、カタログを指定してあげる必要あり
  val optWannaDatabaseTabls = Some(MTable.getTables(Some(config.getString("mysql.wanna.catalog")), Some(""), None, None))
  val model = Await.result(db.run(driver.createModel(tables = optWannaDatabaseTabls)), Duration.Inf)
  println(model.tables)

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