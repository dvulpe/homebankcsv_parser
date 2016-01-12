package ro.dvulpe.ingparser

import scala.io.Source
import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import java.util.Locale

object Parser extends App {

  val source = Source.fromFile("/Users/dan/Downloads/Tranzactii_pe_perioada-5.csv").mkString("")

  implicit def stringToDate(input: String): DateTime =
    DateTimeFormat.forPattern("dd MMMMM yyyy").withLocale(new Locale("RO")).parseDateTime(input)

  val start = System.currentTimeMillis()
  //  val input = "(bla(bla) (bla)(bla)(bla)(bla)(bla))(bla)(bla)"
  //  (1 to 1000000).foreach {
  //    _ =>
  //    _ =>

  implicit val dateOrdering = new Ordering[DateTime] {
    def compare(x: DateTime, y: DateTime): Int =
      x.compareTo(y)
  }

  //  implicit val dateTimeOrdering: Ordering[DateTime] = comparableToOrdering(null)

  val records: List[IngRecord] = INGParser.parseRecords(source).sortBy(_.summary.date)
  val duration = System.currentTimeMillis() - start
  println(records.size)
  records.foreach(r => println(r.asString))
  //      val records = ParenthesisParser(input)
  //      println(records.successful)
  //  }
  println(s"total duration ${duration}ms")

  //  val output = records.map {
  //    rec => (rec.summary.asList ++ List(rec.details.mkString(" "))).map(f => f).mkString(",")
  //  }.mkString("\n")
  //  println(output)
}
