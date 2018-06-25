package com.tiloom6

import akka.pattern.ask
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.util.Timeout
import akka.http.scaladsl.server.Route

import scala.concurrent.{Future, Promise}
import scala.util.{Failure, Success}

/**
  * WannaTagのREST-APIのルーティングを管理するトレイト
  */
trait WannaTagRestApi extends RestApi {
  import WannaTagActor._
  import WannaTagProtocol._
  import com.tiloom6.Tables._
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
          // getWannatagの実行を待って成功ならSuccess, 失敗ならFailure
          onComplete(getWannatag(compare, postDate, limit)) {
            case Success(wannatags) =>
              // Seq[WannatagRow]をSeq[WannaTagGet]に変換する
              complete(OK, wannatags.map(wannatag => WannaTagGet(wannatag.id, wannatag.title, wannatag.body, wannatag.username, wannatag.postDate.getMillis, true)))
            case Failure(e) =>
              complete(InternalServerError, e.getMessage)
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
    val futureResult = wannatagActor ? GetWannaTags(compare, postDate, limit)
    for {
      // Future[Promise[Any]]型からPromise[Any]を抜き取る
      promiseResult <- futureResult
      // Promise[Any]をFuture[Seq[WannatagRow]]に変換してSeq[WannatagRow]を抜き取る
      wannatags <- promiseResult.asInstanceOf[Promise[Any]].future.mapTo[Seq[WannatagRow]]
    } yield wannatags
  }
}
