package ro.dvulpe.ingparser

import scala.util.parsing.combinator._
import scala.util.parsing.input.Reader


trait CSVParser extends RegexParsers {
  override val skipWhitespace = false // meaningful spaces in CSV

  override val whiteSpace = "".r


  override protected def handleWhiteSpace(source: CharSequence, offset: Int): Int =
    offset

  val COMMA = ","
  val DQUOTE = "\""
  val DQUOTE_ESC = "\"\"" ^^^ "\""

  val CRLF = "\r\n" | "\n"
  val TXT = "[^\",\r\n]".r
  val SPACES = "[ \t]+".r

  val escaped = {
    ((SPACES ?) ~> DQUOTE ~> ((COMMA | CRLF | TXT | DQUOTE_ESC) *) <~ DQUOTE <~ (SPACES ?)) ^^ {
      case ls => ls.mkString("")
    }
  }

  val plain = (TXT *) ^^ {
    case ls => ls.mkString
  }

  val field = escaped | plain

  val record = repsep(field, COMMA)

  val file = repsep(record, CRLF) <~ (CRLF ?)

  def parse(s: Reader[Char]) = parseAll(file, s) match {
    case Success(res, _) => res
    case e => throw new Exception(e.toString)
  }
}

object CSVParser extends CSVParser
