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
    * @param wannaTagId wannaTag Id
    */
  case class GetWannaTag(wannaTagId: Int)
}

/**
  * WannaTagアクター
  */
final class WannaTagActor(implicit timeout: Timeout) extends Actor {
  import WannaTagActor._

  private val wannaTagTableActor = context.actorOf(WannaTagTableActor.props, "wannatagTable")
  context.watch(wannaTagTableActor)

  /**
    * WannaTagアクターのレシーバ
    */
  override def receive = {
    case GetWannaTag(wannaTagId) =>
      wannaTagTableActor ! wannaTagId
      sender() ! wannaTagId
  }
}
