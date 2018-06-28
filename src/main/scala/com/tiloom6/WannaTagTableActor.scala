package com.tiloom6

import akka.actor.{Actor, Props}
import akka.util.Timeout
import org.joda.time.DateTime

/**
  * WannaTagDaoアクターのコンパニオンオブジェクト
  */
object WannaTagTableActor {

  /**
    * WannaTagDaoアクターのProps生成
    *
    * @param timeout タイムアウト時間
    */
  def props(implicit timeout: Timeout) = Props(classOf[WannaTagTableActor], timeout)

  /**
    * WannaTag取得パターン用のメッセージ
    *
    * @param isOlder 対象日付より過去のものか
    * @param optTargetDate 対象日付
    * @param optLimit 取得制限数
    */
  case class SelectWannatags(isOlder: Boolean, optTargetDate: Option[DateTime], optLimit: Option[Long])

  /**
    * WannaTagインサート用のメッセージ
    * @param title タイトル
    * @param body ワナタグの内容
    * @param userId ユーザID
    */
  case class InsertWannaTag(title: String, body: String, userId: Long)
}

/**
  * WannaTagDaoActorアクター
  *
  * @param timeout タイムアウト時間
  */
final class WannaTagTableActor(implicit timeout: Timeout) extends Actor {
  import slick.jdbc.MySQLProfile.api._
  import com.tiloom6.Tables._
  import com.tiloom6.WannaTagTableActor._
  import com.typesafe.config.ConfigFactory
  import com.github.tototoshi.slick.MySQLJodaSupport._ // Rep[org.joda.time.DateTime]の拡張メソッドとか暗黙の型変換とかやってくれる

  /** DB情報のコンフィグ設定 */
  private val config = ConfigFactory.load("wannamysql")

  // TODO DBを外部から注入して貰う形にしたい
  /** データベースコネクション */
  private val db = Database.forConfig("mysql.db", config = config)

  private implicit val dispatcher = context.dispatcher

  /**
    * WannaTagDaoアクターのレシーバ
    *
    * GetWannatags -> 取得結果のWannaTagのSeqを取得
    */
  override def receive = {

    case SelectWannatags(isOlder, optTargetDate, optLimit) =>
      // 対象日付を取得する（なければ現在日付）
      val targetDate = optTargetDate.getOrElse(DateTime.now)
      // olderなら対象日付より過去、newerなら対象日付より未来のものを取得する、ソート順は降順（新 -> 古）
      val sortedSelectQuery = (if (isOlder) Wannatag.filter(row => row.postDate <= targetDate) else Wannatag.filter(row => row.postDate >= targetDate)).sortBy(row => row.postDate.desc)
      // 取得制限件数があれば「limit」を付与する
      val query = if (optLimit.isEmpty) sortedSelectQuery else sortedSelectQuery.take(optLimit.get)
      // wannatagテーブルからの取得結果を返す
      sender ! db.run(query.result)

    case InsertWannaTag(title, body, userId) =>
      // TODO usernameはUserテーブルを作ってそこから取ってくる
      val action = (Wannatag returning Wannatag.map(_.id)) += WannatagRow(0, title, body, "", DateTime.now)
      sender ! db.run(action)
  }

}
