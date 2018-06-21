package com.tiloom6

import akka.actor.ActorSystem
import akka.pattern.ask
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.util.Timeout
import akka.http.scaladsl.server.Route

import scala.util.{Failure, Success}

/**
  * WannaTagのREST-APIのルーティングを管理するトレイト
  */
trait WannaTagRestApi extends RestApi {
  import WannaTagActor._
  import WannaTagRequest._

  /** タイムアウト時間 */
  protected implicit val timeout: Timeout

  /** WannaTagのREST-APIを統括するActorSystem */
  protected override implicit val actorSystem: ActorSystem

  /** WannaTagのREST-APIルータ */
  protected override val routes: Route = getWannaTags ~ postWannaTags

  def getWannaTags = pathPrefix("wannatags") {
    pathEndOrSingleSlash {
      get {
        // Getパラメータはこんな感じに受けられる https://doc.akka.io/docs/akka-http/current/routing-dsl/directives/parameter-directives/parameters.html
        parameters('compare ? "older", 'postDate.as[Long] ? -1L, 'limit.as[Int] ? -1L) { (compare, postDate, limit) =>
          // getWannatagの実行を待って成功ならSuccess, 失敗ならFialure
          onComplete(getWannatag) {
            case Success(res) => complete(OK, s"older = $compare, postDate = $postDate, limit = $limit, wannatag = $res")
            case Failure(e) => complete(BadRequest)
          }
        }
      }
    }
  }

  def postWannaTags = pathPrefix("wannatags") {
    pathEndOrSingleSlash {
      post {
        entity(as[WannaTagPost]) { wannaTagPost =>
          complete(OK, s"title = ${wannaTagPost.title}, body = ${wannaTagPost.body} userId = ${wannaTagPost.userId}")
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
