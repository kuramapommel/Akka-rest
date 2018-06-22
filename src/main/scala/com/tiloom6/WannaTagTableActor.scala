package com.tiloom6

import akka.actor.{Actor, Props}
import akka.util.Timeout
import com.typesafe.config.ConfigFactory

object WannaTagTableActor {

  /**
    * WannaTagアクターのProps生成
    *
    * @param timeout タイムアウト時間
    */
  def props(implicit timeout: Timeout) = Props(new WannaTagTableActor)
}

final class WannaTagTableActor extends Actor {
  import slick.jdbc.JdbcBackend._

  private val config = ConfigFactory.load("wannamysql")
  println("config OK")
  private val db = Database.forConfig("mysql.db", config = config)
  println("db OK")

  override def receive = {
    case msg => println(msg)
  }

}
