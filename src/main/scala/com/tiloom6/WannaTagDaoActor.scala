package com.tiloom6

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.tiloom6.WannaTagDaoActor.GetWannatags
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime

import scala.concurrent.Await
import scala.concurrent.duration.Duration

/**
  * WannaTagDaoアクターのコンパニオンオブジェクト
  */
object WannaTagDaoActor {

  /**
    * WannaTagDaoアクターのProps生成
    *
    * @param timeout タイムアウト時間
    */
  def props(implicit timeout: Timeout) = Props(new WannaTagDaoActor)

  /**
    * WannaTag取得パターン用のメッセージ
    *
    * @param isOlder 対象日付より過去のものか
    * @param targetDate 対象日付
    * @param limit 取得制限数
    */
  case class GetWannatags(isOlder: Boolean, targetDate: DateTime, limit: Long)
}

/**
  * WannaTagDaoActorアクター
  *
  * @param timeout タイムアウト時間
  */
final class WannaTagDaoActor(implicit timeout: Timeout) extends Actor {
  import slick.jdbc.MySQLProfile.api._
  import com.tiloom6.Tables._

  /** DB情報のコンフィグ設定 */
  private lazy val config = ConfigFactory.load("wannamysql")

  /** データベースコネクション */
  private lazy val db = Database.forConfig("mysql.db", config = config)

  /**
    * WannaTagDaoアクターのレシーバ
    *
    * GetWannatags -> 取得結果のWannaTagのSeqを取得
    */
  override def receive = {

    case GetWannatags(isOlder, postDate, limit) =>
      // wannatagテーブルからの取得結果を返す
      sender() ! Await.result(db.run(Wannatag.result), timeout.duration)
  }

}
