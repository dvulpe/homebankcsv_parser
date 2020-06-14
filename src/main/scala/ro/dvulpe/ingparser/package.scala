package ro.dvulpe

import java.text.{DecimalFormat, NumberFormat}
import java.time.LocalDate
import java.time.format.{DateTimeFormatter, FormatStyle}
import java.util.Locale

package object ingparser {
  implicit def richString(input: String) = new {
    private val localDateFormatter = DateTimeFormatter.ofLocalizedDate(FormatStyle.LONG).withLocale(new Locale("RO"))

    def asLocalDate = LocalDate.parse(input, localDateFormatter)

    def asDecimal = {
      val format: DecimalFormat = NumberFormat.getInstance(new Locale("RO")).asInstanceOf[DecimalFormat]
      format.setParseBigDecimal(true)
      BigDecimal.apply(format.parse(input).asInstanceOf[java.math.BigDecimal])
    }
  }

}
