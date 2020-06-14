package ro.dvulpe.ingparser

import java.time.LocalDate

case class Transaction(id: String, date: LocalDate, notes: String, amount: BigDecimal,
                       payee: String, accountRef: Option[String], bank: Option[String],
                       reference: String)


