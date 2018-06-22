package com.tiloom6

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

/**
  * REST-APIトレイト
  */
trait RestApi {

  /** REST-APIルータ */
  protected val routes: Route

  /** REST-APIを統括するActorSystem */
  protected implicit val actorSystem: ActorSystem

  /**
    * サーバ起動
    */
  def start() = {
    val host = actorSystem.settings.config.getString("http.host")
    val port = actorSystem.settings.config.getInt("http.port")
    startHttpServer(routes, host, port)
  }

  /**
    * 指定されたhostとpostでサーバを起動する
    *
    * @param api REST-APIルータ
    * @param host host ip
    * @param port 起動するポート番号
    */
  private def startHttpServer(api: Route, host: String, port: Int) = {
    import scala.io._
    import scala.concurrent.ExecutionContext.Implicits.global

    implicit val materializer = ActorMaterializer()
    implicit val dispatcher = actorSystem.dispatcher

    // サーバ起動
    val bindingFuture = Http().bindAndHandle(api, host, port)

    println(s"Server online at host = ${host}, port = ${port}. Press RETURN to stop...")

    // enter押下でサーバ停止
    StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => actorSystem.terminate())
  }

}
