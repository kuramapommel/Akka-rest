package com.tiloom6

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.joda.time.DateTime

import scala.concurrent.duration._
import org.scalatest._

import scala.concurrent.{Await, Future}
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Success, Try}

class WannaTagActorSpec extends TestKit(ActorSystem("testBoxOffice"))
  with WordSpecLike // BDDスタイルのテストを実現
  with MustMatchers // パワーアサート
  with ImplicitSender // Actor側でsenderにメッセージを返している場合にそのメッセージを受け取る
  with StopSystemAfterAll // テスト後にActorSystemを停止する
  {

  "A WannaTag Actor" must {
    import WannaTagActor._
    import com.tiloom6.Tables._

    "return wannaTag when it receives a GetWannaTag message" in {
      import akka.util.Timeout

      val duration = Duration.create("3000 millis")
      implicit val timeout: Timeout = FiniteDuration(duration.length, duration.unit)
      val wannatagActor = system.actorOf(WannaTagActor.props, "wannatag")

      // テスト実行
      wannatagActor ! GetWannaTags("older", 1, 20)

      // アサート
      expectMsgPF() {
        case futureResult: Future[Any] =>

          // Actorからの受け取り結果をFuture[Seq[WannatagRow]]に変換
          val futureWannatags = futureResult.mapTo[Seq[WannatagRow]]

          // futureの処理待ち
          Await.ready(futureWannatags, duration)

          // Futur[Try[Seq[WannatagRow]]] -> WannatagRow 途中で例外が発生したらテストエラーになるだけなのでgetでOK
          val tryWannatags = futureWannatags.value.get
          val wannatags = tryWannatags.get
          val wannatag = wannatags.headOption.get

          // アサーション
          wannatag.title must be("aaa")
          wannatag.body must be("bbb")
          wannatag.username must be("ccc")

        case _ => fail()
      }
    }
  }
}