package com.tiloom6

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.util.Timeout
import akka.http.scaladsl.server.Route

/**
  * WannaTagのREST-APIのルーティングを管理するトレイト
  */
trait WannaTagRestApi extends RestApi {
  import WannaTagActor._

  /** タイムアウト時間 */
  protected implicit val timeout: Timeout

  /** WannaTagのREST-APIを統括するActorSystem */
  protected override implicit val actorSystem: ActorSystem

  /** WannaTagのREST-APIルータ */
  protected override val routes: Route = pathPrefix("Wannatag") {
    pathEndOrSingleSlash {
      get {
        onSuccess(getWannatag) { result =>
          complete(OK, result.toString)
        }
      }
    }
  }

  /** WannaTagのREST-APIのトップレベルActorRef */
  private lazy val wannatag = actorSystem.actorOf(WannaTagActor.props, "wannatag")

  /**
    * WannaTagを取得する
    *
    * @return WannaTag
    */
  private def getWannatag = wannatag.?(GetWannaTag(1)).mapTo[Int]
}
