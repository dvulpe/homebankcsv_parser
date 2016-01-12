package ro.dvulpe.ingparser

import scala.util.parsing.combinator.RegexParsers

object ParenthesisParser extends RegexParsers {
  def term = "[^\\(\\)]+".r

  val expr: Parser[String] = "(" ~ rep(term | expr) ~ ")" ^^ {
    case open ~ l ~ closed => s"$open $l $closed"
  }

  lazy val allExpr: Parser[List[String]] = rep(expr)

  def apply(input: String) = parseAll(allExpr, input)

  def isValid(input: String): Boolean = parseAll(allExpr, input) match {
    case s@Success(_, _) => true
    case _ => false
  }
}
