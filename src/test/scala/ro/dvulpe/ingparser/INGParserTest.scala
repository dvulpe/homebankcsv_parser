package ro.dvulpe.ingparser

import java.time.LocalDate
import org.scalatest.funsuite.AnyFunSuite

class INGParserTest extends AnyFunSuite {

  test("transaction detail line should be parsed") {
    val input = ",Beneficiar: RCS AND RDS SA,,"
    val result = INGParser.parseTest(input, INGParser.transDetail)
    assert(result === "Beneficiar: RCS AND RDS SA")
  }

  test("transaction details lines should be parsed") {
    val input =
      """,Beneficiar: RCS AND RDS SA,,
        |,Banca: INGB CENTRALA,,
        |,Referinta: 70979872,,""".stripMargin
    val result = INGParser.parseTest(input, INGParser.transDetails)
    assert(result === List("Beneficiar: RCS AND RDS SA", "Banca: INGB CENTRALA", "Referinta: 70979872"))
  }

  test("transaction summary line should be parsed") {
    val input = "30 septembrie 2013,Plata debit direct,\"120,19\","
    val result = INGParser.parseTest(input, INGParser.summaryLine)
    assert(result === Summary(LocalDate.of(2013, 9, 30), "Plata debit direct", Some(BigDecimal("120.19")), None))
  }

  test("transaction with details should be parsed") {
    val input =
      """30 septembrie 2013,Plata debit direct,"120,19",
        |,Beneficiar: RCS AND RDS SA,,
        |,Banca: INGB CENTRALA,,
        |,Referinta: 70979872,,""".stripMargin
    val result = INGParser.parseTest(input, INGParser.ingRecord)
    assert(result === IngRecord(
      summary = Summary(
        date = LocalDate.of(2013, 9, 30),
        details = "Plata debit direct", debit = Some(BigDecimal("120.19")), credit = None),
      details = "Beneficiar: RCS AND RDS SA" :: "Banca: INGB CENTRALA" :: "Referinta: 70979872" :: Nil)
    )
  }

  test("transaction without details should be parsed") {
    val input =
      """30 septembrie 2013,Plata debit direct,"120,19",
        |""".stripMargin
    val result = INGParser.parseTest(input, INGParser.ingRecord)
    assert(result === IngRecord(
      summary = Summary(
        date = LocalDate.of(2013, 9, 30),
        details = "Plata debit direct",
        debit = Some(BigDecimal("120.19")),
        credit = None
      ),
      details = Seq.empty
    ))
  }

  test("two consecutive transactions should be parsed") {
    val input =
      """30 septembrie 2013,Plata debit direct,"120,19",
        |30 septembrie 2013,Plata debit direct,"120,19",
        |""".stripMargin
    val result = INGParser.parseRecords(input)
    assert(result === List(
      IngRecord(
        summary = Summary(
          date = LocalDate.of(2013, 9, 30),
          details = "Plata debit direct",
          debit = Some(BigDecimal("120.19")),
          credit = None
        ),
        details = Seq.empty
      ),
      IngRecord(
        summary = Summary(
          date = LocalDate.of(2013, 9, 30),
          details = "Plata debit direct",
          debit = Some(BigDecimal("120.19")),
          credit = None
        ),
        details = Seq.empty
      )
    ))
  }

  test("transaction fiel with header should be matched") {
    val input =
      """Data,Detalii tranzactie,Debit,Credit
        |30 septembrie 2013,Plata debit direct,"120,19",
        |30 septembrie 2013,Plata debit direct,"120,19",
        |""".stripMargin
    val result = INGParser.parseRecords(input)
    assert(result === List(
      IngRecord(
        summary = Summary(
          date = LocalDate.of(2013, 9, 30),
          details = "Plata debit direct",
          debit = Some(BigDecimal("120.19")),
          credit = None
        ),
        details = Seq.empty
      ),
      IngRecord(
        summary = Summary(
          date = LocalDate.of(2013, 9, 30),
          details = "Plata debit direct",
          debit = Some(BigDecimal("120.19")),
          credit = None
        ),
        details = Seq.empty
      )
    ))
  }

}
