package ro.dvulpe.ingparser

import java.time.LocalDate

import scala.language.postfixOps
import scala.util.Try
import scala.util.parsing.combinator._

case class Summary(date: LocalDate, details: String, debit: Option[BigDecimal], credit: Option[BigDecimal]) {
  def asList = date :: details :: debit.getOrElse("") :: credit.getOrElse("") :: Nil

  def asString = s"${date.toString} - $details, Debit: $debit, Credit: $credit"
}

case class IngRecord(summary: Summary, details: Seq[String]) {
  def asString: String = {
    summary.asString + details.map("\t" + _).mkString("\n")
  }
}

object INGParser extends RegexParsers with CSVParser {
  private def mapper[T](f: String => T): String => Parser[T] =
    src => Parser {
      in: Input =>
        Try(f(src)).fold(
          ex => Failure(ex.getMessage, in),
          v => Success(v, in)
        )
    }

  private val date: Parser[LocalDate] = field >> mapper(_.asLocalDate)

  private val decimal: Parser[BigDecimal] = field >> mapper(_.asDecimal)

  val transDetail: Parser[String] = COMMA ~> field <~ repsep(field, COMMA)

  val transDetails: Parser[List[String]] = rep(transDetail <~ (CRLF ?))

  val summaryLine: Parser[Summary] =
    (date <~ COMMA) ~ (field <~ COMMA) ~ (decimal.? <~ COMMA) ~ (decimal ?) ^^ {
      case transactionDate ~ details ~ credit ~ debit =>
        Summary(transactionDate, details, credit, debit)
    }

  val ingRecord: Parser[IngRecord] =
    (summaryLine <~ CRLF) ~ (transDetails ?) ^^ {
      case summary ~ trans =>
        IngRecord(summary, trans.getOrElse(Seq.empty))
    }

  val header: Parser[String] = ("Data" ~> rep1sep(field, COMMA)) ~> CRLF

  val ingRecords: Parser[List[IngRecord]] = header.? ~> rep(ingRecord <~ CRLF.?)

  def parseRecords(s: String): List[IngRecord] = parser(ingRecords, s)

  def parseTest[T](in: String, p: Parser[T]): T = parser(p, in)
}
