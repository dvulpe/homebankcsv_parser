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

  def asDate(data: String) = Parser {
    in: Input =>
      Try(data.asLocalDate) match {
        case scala.util.Success(localDate) => Success(localDate, in)
        case scala.util.Failure(ex) => Failure(ex.toString, in)
      }
  }

  val date: Parser[LocalDate] = field >> asDate

  def asDecimal(data: String) = Parser {
    in: Input =>
      Try(data.asDecimal) match {
        case scala.util.Success(value) => Success(value, in)
        case scala.util.Failure(ex) => Failure(ex.getMessage, in)
      }
  }

  val decimal = field >> asDecimal

  val transDetail = COMMA ~> field <~ repsep(field, COMMA)

  val transDetails = rep(transDetail <~ (CRLF ?))

  val summaryLine =
    (date <~ COMMA) ~ (field <~ COMMA) ~ (decimal.? <~ COMMA) ~ (decimal ?) ^^ {
      case transactionDate ~ details ~ credit ~ debit =>
        Summary(transactionDate, details, credit, debit)
    }

  val ingRecord =
    (summaryLine <~ CRLF) ~ (transDetails ?) ^^ {
      case summary ~ trans =>
        IngRecord(summary, trans.getOrElse(Seq.empty))
    }

  val header = ("Data" ~> rep1sep(field, COMMA)) ~> CRLF

  val ingRecords = header.? ~> rep(ingRecord <~ CRLF.?)

  def parseRecords(s: String): List[IngRecord] = parseAll(ingRecords, s) match {
    case Success(res, _) => res
    case e => throw new Exception(e.toString)
  }

  def parseTest[T](in: String, parser: Parser[T]): T = {
    parseAll(parser, in) match {
      case Success(res, _) => res
      case e => throw new Exception(e.toString)
    }
  }
}
