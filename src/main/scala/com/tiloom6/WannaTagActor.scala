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
  def props(implicit timeout: Timeout) = Props(classOf[WannaTagActor], timeout)

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
  import WannaTagTableActor._
  import LongExt._
  import akka.pattern.ask

  // TODO 子アクターを外部から注入して貰う形にしたい
  /** 子アクターとしてのwannaTagDaoアクター */
  private lazy val wannaTagTableActor = context.actorOf(WannaTagTableActor.props, "wannatagTable")
  private implicit lazy val dispatcher = context.dispatcher
  // 監視対象に追加
  context.watch(wannaTagTableActor)

  /**
    * WannaTagアクターのレシーバ
    *
    * GetWannaTags -> 取得結果のWannaTagのSeqを取得
    */
  override def receive = {

    case GetWannaTags(compare, postDate, limit) =>
      val futureWannatagsResult = wannaTagTableActor ? SelectWannatags(
        compare.equals("older"),
        if (postDate >= 0) Some(postDate.toDatetime) else None,
        if (limit >= 0) Some(limit) else None
      )

      sender ! futureWannatagsResult
  }
}
