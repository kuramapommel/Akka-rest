package com.tiloom6

import spray.json.DefaultJsonProtocol

object WannaTagRequest extends DefaultJsonProtocol {

  final case class WannaTagPost(title: String, body: String, userId: Long)

  implicit val wannaTagPostProtocol = jsonFormat3(WannaTagPost)

}
