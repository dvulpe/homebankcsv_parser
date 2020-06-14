package ro.dvulpe.ingparser

import org.scalatest.FunSuite
import org.joda.time.{DateTime, LocalDate, LocalDateTime}

class INGParserTest extends FunSuite {

  test("transaction detail line should be parsed") {
    val input = ",Beneficiar: RCS AND RDS SA,,"
    val result = INGParser.parseTest(input, INGParser.transDetail)
    assert(result === "Beneficiar: RCS AND RDS SA")
  }

  test("transaction details lines should be parsed") {
    val input = ",Beneficiar: RCS AND RDS SA,,\n,Banca: INGB CENTRALA,,\n,Referinta: 70979872,,"
    val result = INGParser.parseTest(input, INGParser.transDetails)
    assert(result === List("Beneficiar: RCS AND RDS SA", "Banca: INGB CENTRALA", "Referinta: 70979872"))
  }

  test("transaction summary line should be parsed") {
    val input = "30 septembrie 2013,Plata debit direct,\"120,19\","
    val result = INGParser.parseTest(input, INGParser.summaryLine)
    assert(result === Summary(new LocalDate("2013-09-30"), "Plata debit direct", Some(BigDecimal("120.19")), None))
  }

  test("transaction with details should be parsed") {
    val input = "30 septembrie 2013,Plata debit direct,\"120,19\",\n,Beneficiar: RCS AND RDS SA,,\n,Banca: INGB CENTRALA,,\n,Referinta: 70979872,,"
    val result = INGParser.parseTest(input, INGParser.ingRecord)
    assert(result != null)
  }

  test("transaction without details should be parsed") {
    val input = "30 septembrie 2013,Plata debit direct,\"120,19\",\n"
    val result = INGParser.parseTest(input, INGParser.ingRecord)
    assert(result != null)
  }

  test("two consecutive transactions should be parsed") {
    val input = "30 septembrie 2013,Plata debit direct,\"120,19\",\n30 septembrie 2013,Plata debit direct,\"120,19\",\n"
    val result = INGParser.parseRecords(input)
    assert(result.size === 2)
  }

  test("header should be matched") {
    val input = "Data,Detalii tranzactie,Debit,Credit\n"
    val result = INGParser.parseTest(input, INGParser.header)
    assert(result != null)
  }

  test("transaction fiel with header should be matched") {
    val input = "Data,Detalii tranzactie,Debit,Credit\n30 septembrie 2013,Plata debit direct,\"120,19\",\n30 septembrie 2013,Plata debit direct,\"120,19\",\n"
    val result = INGParser.parseRecords(input)
    assert(result.size === 2)
  }

}
