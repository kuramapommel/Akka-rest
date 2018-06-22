package com.tiloom6

import akka.actor.ActorSystem
import com.typesafe.config.{Config, ConfigFactory}

/**
  * WannaTagのREST-APIを起動するためのメインクラス
  */
object WannaTagMain extends App {

  /** wannatag.confから取得したコンフィグ設定 */
  private val wannatagConfig = ConfigFactory.load("wannatag")

  /** WannaTagのREST-APIを統括するActorSystem */
  private val wannatagActorSystem = ActorSystem("wannatag", wannatagConfig)

  /** タイムアウト設定 */
  private val requestTimeout = new RequestTimeout {
    protected override val config = wannatagConfig
  }

  /** WannaTagのREST-API本体 */
  private val api = new WannaTagRestApi {
    protected override implicit val timeout = requestTimeout.configuredRequestTimeout
    protected override implicit val actorSystem = wannatagActorSystem
  }

  // REST-APIの起動
  api.start()
}