package ro.dvulpe.ingparser

import org.scalatest.{FunSuite, Matchers}

class ParenthesisParserTest extends FunSuite with Matchers {
  test("balanced parentheses") {
    ParenthesisParser.isValid("()") should equal (true)
  }
  test("unbalanced parentheses") {
    ParenthesisParser.isValid(")((test)test(test2)))") should equal (false)
  }

}
