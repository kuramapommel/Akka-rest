package com.tiloom6

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.server.Route
import akka.stream.ActorMaterializer

/**
  * サーバ起動トレイト
  */
trait StartUp extends RequestTimeout {

  /**
    * サーバ起動
    *
    * @param api REST-APIルータ
    * @param system REST-APIを統括するActorSystem
    */
  protected def startUp(api: Route)(implicit system: ActorSystem) = {
    val host = system.settings.config.getString("http.host")
    val port = system.settings.config.getInt("http.port")
    startHttpServer(api, host, port)
  }

  /**
    * 指定されたhostとpostでサーバを起動する
    *
    * @param api REST-APIルータ
    * @param host host ip
    * @param port 起動するポート番号
    * @param system REST-APIを統括するActorSystem
    */
  private def startHttpServer(api: Route, host: String, port: Int)(implicit system: ActorSystem) = {
    import scala.io._
    import scala.concurrent.ExecutionContext.Implicits.global

    implicit val materializer = ActorMaterializer()

    // サーバ起動
    val bindingFuture = Http().bindAndHandle(api, host, port)

    println(s"Server online at host = ${host}, port = ${port}. Press RETURN to stop...")

    // enter押下でサーバ停止
    StdIn.readLine()
    bindingFuture.flatMap(_.unbind()).onComplete(_ => system.terminate())
  }

}
