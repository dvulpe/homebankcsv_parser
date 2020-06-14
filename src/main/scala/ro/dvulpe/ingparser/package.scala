package ro.dvulpe

import org.joda.time.format.DateTimeFormat
import java.util.Locale
import java.text.{NumberFormat, DecimalFormat}

package object ingparser {
  implicit def richString(input: String) = new {
    def asDateTime = DateTimeFormat.forPattern("dd MMMMM yyyy")
      .withLocale(new Locale("RO"))
      .parseDateTime(input)
      .toLocalDate

    def asDecimal = {
      val format: DecimalFormat = NumberFormat.getInstance(new Locale("RO")).asInstanceOf[DecimalFormat]
      format.setParseBigDecimal(true)
      BigDecimal.apply(format.parse(input).asInstanceOf[java.math.BigDecimal])
    }
  }

}
