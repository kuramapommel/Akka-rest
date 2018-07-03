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

  /** Future処理を実行するExecutionContext */
  protected implicit lazy val dispatcher = actorSystem.dispatcher

  /** Streamを実行するためのマテリアライザ */
  private implicit lazy val materializer = ActorMaterializer()

  /**
    * サーバ起動
    */
  def start() = {
    val config = actorSystem.settings.config
    startHttpServer(routes, config.getString("http.host"), config.getInt("http.port"))
  }

  /**
    * 指定されたhostとportでサーバを起動する
    *
    * @param api REST-APIルータ
    * @param host host ip
    * @param port 起動するポート番号
    */
  private def startHttpServer(api: Route, host: String, port: Int) = {
    import scala.io._

    // サーバ起動
    val bindingFuture = Http().bindAndHandle(api, host, port)

    println(s"Server online at host = $host, port = $port. Press RETURN to stop...")

    // enter押下でサーバ停止
    StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => actorSystem.terminate())
  }

}
