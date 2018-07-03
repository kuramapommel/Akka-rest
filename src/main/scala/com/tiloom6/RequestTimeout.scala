package com.tiloom6

import akka.util.Timeout
import com.typesafe.config.Config

/**
  * タイムアウト時間管理トレイト
  */
trait RequestTimeout {
  import scala.concurrent.duration._

  /** コンフィグ設定 */
  protected val config: Config

  /**
    * コンフィグ設定からタイムアウト時間を取得する
    *
    * @return タイムアウト時間
    */
  def configuredRequestTimeout: Timeout = {
    // AkkaHttp-coreのデフォルトタイムアウトは20秒
    val requestTimeout = config.getString("akka.http.server.request-timeout")
    val duration = Duration(requestTimeout)
    FiniteDuration(duration.length, duration.unit)
  }

}
