package ro.dvulpe.ingparser

import org.joda.time.DateTime

case class Transaction(id: String, date: DateTime, notes: String, amount: BigDecimal,
                       payee: String, accountRef: Option[String], bank: Option[String],
                       reference: String)


