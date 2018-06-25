package com.tiloom6

import akka.actor._
import akka.util._

import scala.concurrent.Future

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
    * @param limit wannaTag Id
    */
  case class GetWannaTags(compare: String, postDate: Long, limit: Long)
}

/**
  * WannaTagアクター
  */
final class WannaTagActor(implicit timeout: Timeout) extends Actor {
  import WannaTagActor._
  import WannaTagDaoActor._
  import LongExt._
  import akka.pattern.ask

  private val wannaTagTableActor = context.actorOf(WannaTagDaoActor.props, "wannatagTable")
  context.watch(wannaTagTableActor)

  /**
    * WannaTagアクターのレシーバ
    */
  override def receive = {
    case GetWannaTags(compare, postDate, limit) =>
      val futureWannatagsResult = wannaTagTableActor ? GetWannatags(compare.equals("newer"), postDate.toDatetime, limit)
      sender() ! futureWannatagsResult
  }
}
