package com.tiloom6

import akka.actor.ActorSystem
import akka.testkit.{ImplicitSender, TestKit}

import scala.concurrent.duration._
import org.scalatest._

class WannaTagActorSpec extends TestKit(ActorSystem("testBoxOffice"))
  with WordSpecLike // BDDスタイルのテストを実現
  with MustMatchers // パワーアサート
  with ImplicitSender // Actor側でsenderにメッセージを返している場合にそのメッセージを受け取る
  with StopSystemAfterAll // テスト後にActorSystemを停止する
  {
  import WannaTagActor._

  "A WannaTag Actor" must {
    "return wannaTag when it receives a GetWannaTag message" in {
      import akka.util.Timeout

      val duration = Duration.create("3000 millis")
      implicit val timeout: Timeout = FiniteDuration(duration.length, duration.unit)
      val wannatagActor = system.actorOf(WannaTagActor.props, "wannatag")

      wannatagActor ! GetWannaTag(1)
      expectMsg(1)
    }
  }
}