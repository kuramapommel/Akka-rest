package com.tiloom6

import akka.pattern.ask
import akka.http.scaladsl.server.Directives._
import akka.http.scaladsl.model.StatusCodes._
import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport._
import akka.util.Timeout
import akka.http.scaladsl.server.Route

import scala.concurrent.{Await, Future, Promise}
import scala.util.{Failure, Success}

/**
  * WannaTagのREST-APIのルーティングを管理するトレイト
  *
  * https://github.com/TILoom6/TILoom6doc/blob/master/til/3rd/wish_list/from_frondend.md
  */
trait WannaTagRestApi extends RestApi {
  import WannaTagActor._
  import WannaTagProtocol._
  import com.tiloom6.Tables._

  /** タイムアウト時間 */
  protected implicit val timeout: Timeout

  /** WannaTagのREST-APIルータ */
  protected override val routes: Route = getWannaTags ~ postWannaTags

  /**
    * WannaTag一覧を取得する
    *
    * GET /wannatags
    */
  private def getWannaTags = pathPrefix("wannatags") {
    pathEndOrSingleSlash {
      get {
        // Getパラメータはこんな感じに受けられる https://doc.akka.io/docs/akka-http/current/routing-dsl/directives/parameter-directives/parameters.html
        parameters('compare ? "older", 'postDate.as[Long] ? -1L, 'limit.as[Int] ? -1L) { (compare, postDate, limit) =>
          // getWannatagの実行を待って成功ならSuccess, 失敗ならFailure
          // TODO エラーハンドリングもうちょっとちゃんとしたい
          onComplete(validateGetParameter(compare).flatMap(_ => getWannatag(compare, postDate, limit))) {
            case Success(wannatags) =>
              // Seq[WannatagRow]をSeq[WannaTagGet]に変換してjsonにして返す
              complete(OK, wannatags.map(wannatag => WannaTagGet(wannatag.id, wannatag.title, wannatag.body, wannatag.username, wannatag.postDate.getMillis, true)))
            case Failure(e) =>
              complete(InternalServerError, e.getMessage)
          }
        }
      }
    }
  }

  /**
    * WannaTagを登録する
    *
    * POST /wannatags
    */
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
    * GET のバリデーションチェック
    *
    * @param compare newer or older
    * @return チェック結果
    */
  private def validateGetParameter(compare: String) = {
    compare match {
      case "older" | "newer" => Future.successful()
      case _ => throw new Exception("Validation Error!!")
    }
  }

  /**
    * WannaTagを取得する
    *
    * @return WannaTag
    */
  private def getWannatag(compare: String, postDate: Long, limit: Long) = {
    val futureResult = wannatagActor ? GetWannaTags(compare, postDate, limit)
    val futureWannaTags = for {
      // TODO どこからPromise型が登場したのかは不明
      futurePromiseWannatags <- futureResult.mapTo[Future[Promise[Seq[WannatagRow]]]]
      promiseWannatags <- futurePromiseWannatags
      wannatags <- promiseWannatags.future
    } yield wannatags

    Await.ready(futureWannaTags, timeout.duration)
    futureWannaTags
  }
}
