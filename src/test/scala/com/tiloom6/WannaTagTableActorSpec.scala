package com.tiloom6

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}
import org.scalatest.{MustMatchers, WordSpecLike}

import scala.concurrent.duration.{Duration, FiniteDuration}
import scala.concurrent.{Await, Future}

class WannaTagTableActorSpec extends TestKit(ActorSystem("testWannaTagTable"))
  with WordSpecLike // BDDスタイルのテストを実現
  with MustMatchers // パワーアサート
  with ImplicitSender // Actor側でsenderにメッセージを返している場合にそのメッセージを受け取る
  with StopSystemAfterAll // テスト後にActorSystemを停止する
  {

  "A WannaTagTable Actor" must {
    "insert wannatag when it receives a InsertWannaTag message" in {
      import akka.util.Timeout
      import com.tiloom6.WannaTagTableActor.InsertWannaTag

      val duration = Duration.create("1000 millis")
      implicit val timeout: Timeout = FiniteDuration(duration.length, duration.unit)

      val wannaTagTableActor = system.actorOf(WannaTagTableActor.props, "wannatagTable")

      wannaTagTableActor ! InsertWannaTag(title = "test wannatag", body = "test body", userId = 1)
      expectMsgPF(){
        case futureResult: Future[Any] =>
          val futureUserId = futureResult.mapTo[Long]

          Await.ready(futureUserId, duration)

          val userId = futureUserId.value.get

          userId must be(1)
        case _ => fail()
      }
    }
  }
}
