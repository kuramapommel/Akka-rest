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
  protected override val routes: Route = responseWannaTags

  def responseWannaTags = pathPrefix("wannatags") {
    pathEndOrSingleSlash {
      get {
        //parameters('older.as[String], 'postDate.as[Long], 'limit.as[Int]) {(older, postDate, limit) =>
        //  complete(OK, s"older = ${older}, postDate = ${postDate}, limit = ${limit}")
        //}
        onSuccess(getWannatag) { res =>
          complete(OK, res.toString)
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
  private def getWannatag = {
    // Future[Any]型で受け取る
    val futureResult = wannatag ? GetWannaTag(1)
    futureResult.mapTo[Int]
  }
}
