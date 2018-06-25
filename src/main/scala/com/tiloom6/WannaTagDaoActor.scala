package com.tiloom6

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.tiloom6.WannaTagDaoActor.GetWannatags
import com.typesafe.config.ConfigFactory
import org.joda.time.DateTime

import scala.concurrent.Await
import scala.concurrent.duration.Duration

object WannaTagDaoActor {

  /**
    * WannaTagアクターのProps生成
    *
    * @param timeout タイムアウト時間
    */
  def props(implicit timeout: Timeout) = Props(new WannaTagDaoActor)

  case class GetWannatags(isOlder: Boolean, postDate: DateTime, limit: Long)
}

final class WannaTagDaoActor extends Actor {
  import slick.jdbc.MySQLProfile.api._
  import com.tiloom6.Tables._

  private val config = ConfigFactory.load("wannamysql")
  private val db = Database.forConfig("mysql.db", config = config)

  override def receive = {
    case GetWannatags(isOlder, postDate, limit) =>
      sender() ! Await.result(db.run(Wannatag.result), Duration.Inf)
  }

}
