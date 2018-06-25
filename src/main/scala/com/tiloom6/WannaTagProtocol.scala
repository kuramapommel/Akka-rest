package com.tiloom6

import spray.json.DefaultJsonProtocol

object WannaTagProtocol extends DefaultJsonProtocol {

  final case class WannaTagPost(title: String, body: String, userId: Long)
  final case class WannaTagGet(wannatagId: Long, title: String, body: String, username: String, postDate: Long, isOwner: Boolean)

  implicit lazy val wannaTagPostProtocol = jsonFormat3(WannaTagPost)
  implicit lazy val wannaTagsGetProtocol = jsonFormat6(WannaTagGet)

}
