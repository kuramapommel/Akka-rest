package com.tiloom6

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}

import scala.concurrent.duration._
import org.scalatest._

import scala.concurrent.{Await, Future, Promise}

class WannaTagActorSpec extends TestKit(ActorSystem("testWannaTag"))
  with WordSpecLike // BDDスタイルのテストを実現
  with MustMatchers // パワーアサート
  with ImplicitSender // Actor側でsenderにメッセージを返している場合にそのメッセージを受け取る
  with StopSystemAfterAll // テスト後にActorSystemを停止する
  {

  implicit val dispatcher = system.dispatcher

  "A WannaTag Actor" must {
    import WannaTagActor._
    import com.tiloom6.Tables._

    "return wannaTag when it receives a GetWannaTag message" in {
      import akka.util.Timeout

      val duration = Duration.create("3000 millis")
      implicit val timeout: Timeout = FiniteDuration(duration.length, duration.unit)
      // TODO 子アクターのMockを渡す形にしたい
      val wannatagActor = system.actorOf(WannaTagActor.props, "wannatag")

      // テスト実行
      wannatagActor ! GetWannaTags("newer", 0, 1)

      // アサート
      expectMsgPF() {
        case futureResult: Future[Any] =>

          // Actorからの受け取り結果をFuture[Promise[Seq[WannatagRow]]]に変換
          val futurePromiseWannatags = futureResult.mapTo[Promise[Seq[WannatagRow]]]
          val futureWannatags = futurePromiseWannatags.flatMap(p => p.future)

          // futureの処理待ち
          Await.ready(futureWannatags, duration)

          // Futur[Try[Seq[WannatagRow]]] -> WannatagRow 途中で例外が発生したらテストエラーになるだけなのでgetでOK
          val wannatags = futureWannatags.value.get.get

          // アサーション
          wannatags.length must be(1)

        case _ => fail()
      }
    }
  }
}