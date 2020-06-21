package ro.dvulpe.ingparser

import scala.language.postfixOps
import scala.util.matching.Regex
import scala.util.parsing.combinator._


trait CSVParser extends RegexParsers {
  override val skipWhitespace = false // meaningful spaces in CSV

  override val whiteSpace: Regex = "".r


  override protected def handleWhiteSpace(source: CharSequence, offset: Int): Int =
    offset

  val COMMA = ","
  val DQUOTE = "\""
  val DQUOTE_ESC: Parser[String] = "\"\"" ^^^ "\""

  val CRLF: Parser[String] = "\r\n" | "\n"
  val TXT: Regex = "[^\",\r\n]".r
  val SPACES: Regex = "[ \t]+".r

  val escaped: Parser[String] = {
    ((SPACES ?) ~> DQUOTE ~> ((COMMA | CRLF | TXT | DQUOTE_ESC) *) <~ DQUOTE <~ (SPACES ?)) ^^ (ls => ls.mkString(""))
  }

  val plain: Parser[String] = (TXT *) ^^ (ls => ls.mkString)

  val field: Parser[String] = escaped | plain

  val record: Parser[List[String]] = repsep(field, COMMA)

  val file: Parser[List[List[String]]] = repsep(record, CRLF) <~ (CRLF ?)

  def parse(s: String): List[List[String]] = parser(file, s)

  protected def parser[T](p: Parser[T], in: String): T = parseAll(p, in) match {
    case Success(res, _) => res
    case e => throw new Exception(e.toString)
  }
}

object CSVParser extends CSVParser
