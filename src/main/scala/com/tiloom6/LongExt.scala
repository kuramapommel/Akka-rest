package com.tiloom6

/**
  * Long型拡張
  */
object LongExt {

  /**
    * Long型拡張クラス
    */
  implicit class LongToJodaDateTime(long: Long) {
    import org.joda.time.DateTime

    /**
      * Long to org.joda.time.DateTime
      *
      * @return org.joda.time.DateTime
      */
    def toDatetime: DateTime = {
      new DateTime(long)
    }
  }
}
