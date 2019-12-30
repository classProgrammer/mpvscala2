package exercises.three.util

import java.text.SimpleDateFormat
import java.util.Calendar
import exercises.three.util.TemperatureUnit.TemperatureUnit

object TemperatureUnit extends Enumeration {
  type TemperatureUnit = Value
  val Celsius, Kelvin, Fahrenheit = Value
}

class MeasurementGenerator(val lower: Float = -40.0f, val upper: Float = 40.0f, val unit: TemperatureUnit = TemperatureUnit.Celsius) {
  private val timeFormat = new SimpleDateFormat("hh:mm:ss:SSS")
  private val rand = scala.util.Random

  private def getTemperature(lower: Float, upper: Float): Float = {
      rand.nextFloat() * (upper - lower) + lower
  }

  private def getTimestamp(): String = {
      timeFormat.format(Calendar.getInstance().getTime())
  }

  def getMeasurement(): (String, Float, TemperatureUnit) = {
    Tuple3(getTimestamp(), getTemperature(lower, upper), unit)
  }
}
