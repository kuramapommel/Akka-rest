package com.tiloom6

import akka.actor._
import akka.util._

/**
  * WannaTagアクターのコンパニオンオブジェクト
  */
object WannaTagActor {

  /**
    * WannaTagアクターのProps生成
    *
    * @param timeout タイムアウト時間
    */
  def props(implicit timeout: Timeout) = Props(new WannaTagActor)

  /**
    * WannaTag取得パターン用のメッセージ
    *
    * @param compare older or newer
    * @param postDate 現在
    * @param limit 取得制限数
    */
  case class GetWannaTags(compare: String, postDate: Long, limit: Long)
}

/**
  * WannaTagアクター
  *
  * @param timeout タイムアウト時間
  */
final class WannaTagActor(implicit timeout: Timeout) extends Actor {
  import WannaTagActor._
  import WannaTagDaoActor._
  import LongExt._
  import akka.pattern.ask

  /** 子アクターとしてのwannaTagDaoアクター */
  private lazy val wannaTagDaoActor = context.actorOf(WannaTagDaoActor.props, "wannatagTable")
  // 監視対象に追加
  context.watch(wannaTagDaoActor)

  /**
    * WannaTagアクターのレシーバ
    *
    * GetWannaTags -> 取得結果のWannaTagのSeqを取得
    */
  override def receive = {

    case GetWannaTags(compare, postDate, limit) =>
      val futureWannatagsResult = wannaTagDaoActor ? GetWannatags(compare.equals("newer"), postDate.toDatetime, limit)
      sender() ! futureWannatagsResult
  }
}
