package com.tiloom6

import akka.actor.ActorSystem
import com.typesafe.config.ConfigFactory

/**
  * WannaTagのREST-APIを起動するためのメインクラス
  */
object WannaTagMain extends App {

  /** wannatag.confから取得したコンフィグ設定 */
  private val config = ConfigFactory.load("wannatag")

  /** WannaTagのREST-APIを統括するActorSystem */
  private val wannatagActorSystem = ActorSystem("wannatag", config)

  /** WannaTagのREST-API本体 */
  private val api = new WannaTagRestApi() {
    protected override implicit val timeout = configuredRequestTimeout(config)
    protected override implicit val actorSystem = wannatagActorSystem
  }

  // REST-APIの起動
  api.start()
}