package exercises.one.util

import java.text.SimpleDateFormat
import java.util.Calendar

object PCUtil {
  val timeFormat = new SimpleDateFormat("hh:mm:ss:SSS")

  def now(): String = {
    timeFormat.format(Calendar.getInstance().getTime())
  }
}
