package com.tiloom6
// AUTO-GENERATED Slick data model
/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = slick.jdbc.MySQLProfile
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {
  val profile: slick.jdbc.JdbcProfile
  import profile.api._
  import com.github.tototoshi.slick.MySQLJodaSupport._
  import org.joda.time.DateTime
  import slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val schema: profile.SchemaDescription = Wannatag.schema
  @deprecated("Use .schema instead of .ddl", "3.0")
  def ddl = schema

  /** Entity class storing rows of table Wannatag
   *  @param id Database column id SqlType(BIGINT UNSIGNED), AutoInc, PrimaryKey
   *  @param title Database column title SqlType(VARCHAR), Length(255,true), Default()
   *  @param body Database column body SqlType(VARCHAR), Length(255,true), Default()
   *  @param username Database column username SqlType(VARCHAR), Length(255,true), Default()
   *  @param postDate Database column post_date SqlType(DATETIME) */
  case class WannatagRow(id: Long, title: String = "", body: String = "", username: String = "", postDate: DateTime)
  /** GetResult implicit for fetching WannatagRow objects using plain SQL queries */
  implicit def GetResultWannatagRow(implicit e0: GR[Long], e1: GR[String], e2: GR[DateTime]): GR[WannatagRow] = GR{
    prs => import prs._
    WannatagRow.tupled((<<[Long], <<[String], <<[String], <<[String], <<[DateTime]))
  }
  /** Table description of table wannatag. Objects of this class serve as prototypes for rows in queries. */
  class Wannatag(_tableTag: Tag) extends profile.api.Table[WannatagRow](_tableTag, Some("wanna"), "wannatag") {
    def * = (id, title, body, username, postDate) <> (WannatagRow.tupled, WannatagRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (Rep.Some(id), Rep.Some(title), Rep.Some(body), Rep.Some(username), Rep.Some(postDate)).shaped.<>({r=>import r._; _1.map(_=> WannatagRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column id SqlType(BIGINT UNSIGNED), AutoInc, PrimaryKey */
    val id: Rep[Long] = column[Long]("id", O.AutoInc, O.PrimaryKey)
    /** Database column title SqlType(VARCHAR), Length(255,true), Default() */
    val title: Rep[String] = column[String]("title", O.Length(255,varying=true), O.Default(""))
    /** Database column body SqlType(VARCHAR), Length(255,true), Default() */
    val body: Rep[String] = column[String]("body", O.Length(255,varying=true), O.Default(""))
    /** Database column username SqlType(VARCHAR), Length(255,true), Default() */
    val username: Rep[String] = column[String]("username", O.Length(255,varying=true), O.Default(""))
    /** Database column post_date SqlType(DATETIME) */
    val postDate: Rep[DateTime] = column[DateTime]("post_date")
  }
  /** Collection-like TableQuery object for table Wannatag */
  lazy val Wannatag = new TableQuery(tag => new Wannatag(tag))
}
