package com.tiloom6

import akka.actor.ActorSystem
import akka.http.scaladsl.server.Route

/**
  * REST-APIのルーティングを管理するトレイト
  */
trait RestApi extends StartUp {

  /** REST-APIルータ */
  protected val routes: Route

  /** REST-APIを統括するActorSystem */
  protected implicit val actorSystem: ActorSystem

  /**
    * REST-APIサーバを起動する
    */
  def start() = {
    startUp(routes)
  }
}


