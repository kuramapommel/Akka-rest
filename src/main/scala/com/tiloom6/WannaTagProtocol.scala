package com.tiloom6

import spray.json.DefaultJsonProtocol

/**
  * WannaTagのjsonのシリアライズ・デシリアライズ定義
  */
object WannaTagProtocol extends DefaultJsonProtocol {

  /**
    * "POST wannatags"時のデシリアライズ用ケースクラス
    */
  final case class WannaTagPost(title: String, body: String, userId: Long)

  /**
    * "GET wannatags"時のシリアライズ用ケースクラス
    */
  final case class WannaTagGet(wannatagId: Long, title: String, body: String, username: String, postDate: Long, isOwner: Boolean)

  /** WannaTagPostのシリアライズ・デシリアライズ定義 */
  implicit lazy val wannaTagPostProtocol = jsonFormat3(WannaTagPost)

  /** WannaTagGetのシリアライズ・デシリアライズ定義 */
  implicit lazy val wannaTagsGetProtocol = jsonFormat6(WannaTagGet)

}
