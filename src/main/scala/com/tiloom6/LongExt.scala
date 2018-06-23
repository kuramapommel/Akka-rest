package com.tiloom6

object LongExt {
  implicit class LongToJodaDateTime(long: Long) {
    import org.joda.time.DateTime
    def toDatetime: DateTime = {
      new DateTime(long)
    }
  }
}
