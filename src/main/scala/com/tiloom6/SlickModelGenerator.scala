package com.tiloom6

import com.typesafe.config.ConfigFactory
import slick.codegen.SourceCodeGenerator
import slick.jdbc.meta.MTable
import slick.{model => SlickModel}

import scala.concurrent.Await
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration.Duration

/**
  * Databaseからテーブル情報を取得してmodelクラスを生成する
  */
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

/**
  * カスタムジェネレータ
  * * java.sql.Timestampをorg.joda.time.DateTimeに置き換える
  */
case class CustomSourceCodeGenerator( model: SlickModel.Model ) extends SourceCodeGenerator( model ) {

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