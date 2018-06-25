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

  private val wannamysqlConfig = ConfigFactory.load("wannamysql")
  private val slickcodegenConfig = ConfigFactory.load("slickcodegen")
  private val db = Database.forConfig("mysql.db", config = wannamysqlConfig)
  private val driver = slick.jdbc.MySQLProfile

  // tablesを指定してあげる、MySQLProfileのデフォルトはカタログの指定がないため、カタログを指定してあげる必要あり
  private val optWannaDatabaseTabls = Some(MTable.getTables(Some(wannamysqlConfig.getString("mysql.wanna.catalog")), Some(""), None, None))
  private val model = Await.result(db.run(driver.createModel(tables = optWannaDatabaseTabls)), Duration.Inf)
  println(model.tables)

  // 自動生成クラス作成
  CustomSourceCodeGenerator(model).writeToFile(
    slickcodegenConfig.getString("option.profile"),
    slickcodegenConfig.getString("option.output_folder"),
    slickcodegenConfig.getString("option.class_package")
  )

}

/**
  * カスタムジェネレータ
  * * java.sql.Timestampをorg.joda.time.DateTimeに置き換える
  */
case class CustomSourceCodeGenerator( model: SlickModel.Model ) extends SourceCodeGenerator( model ) {

  // importの追加
  override def code = "import com.github.tototoshi.slick.MySQLJodaSupport._\n" + "import org.joda.time.DateTime\n" + super.code

  // java.sql.Timestampカラムの場合はDateTimeに書き換える
  override def Table = new Table( _ ) {
    override def Column = new Column( _ ) {
      override def rawType = model.tpe match {
        case "java.sql.Timestamp" => "DateTime"
        case _ => super.rawType
      }
    }
  }
}