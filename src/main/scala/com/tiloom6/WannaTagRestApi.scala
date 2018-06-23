package com.tiloom6

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
  import scala.concurrent.ExecutionContext.Implicits.global

  /** タイムアウト時間 */
  protected implicit val timeout: Timeout

  /** WannaTagのREST-APIルータ */
  protected override val routes: Route = getWannaTags ~ postWannaTags

  private def getWannaTags = pathPrefix("wannatags") {
    pathEndOrSingleSlash {
      get {
        // Getパラメータはこんな感じに受けられる https://doc.akka.io/docs/akka-http/current/routing-dsl/directives/parameter-directives/parameters.html
        parameters('compare ? "older", 'postDate.as[Long] ? -1L, 'limit.as[Int] ? -1L) { (compare, postDate, limit) =>
          // getWannatagの実行を待って成功ならSuccess, 失敗ならFialure
          onComplete(getWannatag(compare, postDate, limit)) {
            case Success(res) => complete(OK, s"older = $compare, postDate = $postDate, limit = $limit, wannatag = $res")
            case Failure(e) => complete(BadRequest)
          }
        }
      }
    }
  }

  private def postWannaTags = pathPrefix("wannatags") {
    pathEndOrSingleSlash {
      post {
        entity(as[WannaTagPost]) { wannaTagPost =>
          complete(OK, s"title = ${wannaTagPost.title}, body = ${wannaTagPost.body} userId = ${wannaTagPost.userId}")
        }
      }
    }
  }

  /** WannaTagのREST-APIのトップレベルActorRef */
  private lazy val wannatagActor = actorSystem.actorOf(WannaTagActor.props, "wannatag")

  /**
    * WannaTagを取得する
    *
    * @return WannaTag
    */
  private def getWannatag(compare: String, postDate: Long, limit: Long) = {
    // Future[Any]型で受け取る
    val futureResult = wannatagActor ? GetWannaTags(compare, postDate, limit)
    futureResult.mapTo[Int]
  }
}
