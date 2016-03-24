package models.org.ludwiggj.finance.application

import java.io.{File, FilenameFilter}
import models.org.ludwiggj.finance.Transaction
import models.org.ludwiggj.finance.persistence.database.Tables.{Funds, Holdings, Prices, Transactions, Users}
import models.org.ludwiggj.finance.persistence.database.{HoldingsDatabase, PricesDatabase}
import models.org.ludwiggj.finance.persistence.file.{FileHoldingFactory, FilePriceFactory, FileTransactionFactory}
import play.api.Play
import play.api.Play.current
import play.api.db.DB
import play.api.test.FakeApplication
import scala.slick.driver.MySQLDriver.simple._
import scala.util.matching.Regex

object DatabaseReload extends App {

  def isAFileWithUserNameMatching(pattern: Regex) = new FilenameFilter {
    override def accept(dir: File, name: String) = {
      name match {
        case pattern(userName) => true
        case _ => false
      }
    }
  }

  def isAFileWithoutUserNameMatching(pattern: Regex) = new FilenameFilter {
    override def accept(dir: File, name: String) = {
      pattern.findFirstIn(name).isDefined
    }
  }

  def deleteAllRows() = {
    lazy val db = Database.forDataSource(DB.getDataSource("finance"))
    val users: TableQuery[Users] = TableQuery[Users]
    val funds: TableQuery[Funds] = TableQuery[Funds]
    val prices: TableQuery[Prices] = TableQuery[Prices]
    val holdings: TableQuery[Holdings] = TableQuery[Holdings]
    val transactions: TableQuery[Transactions] = TableQuery[Transactions]


    db.withSession {
      implicit session =>
        transactions.delete
        holdings.delete
        prices.delete
        funds.delete
        users.delete
    }
  }

  // TODO - use generics to replace FileHoldingFactory, FileTransactionFactory & FilePriceFactory with a single class
  def reloadHoldings() = {
    val holdingPattern = """holdings.*_([a-zA-Z]+)\.txt""".r
    for (
      aFile <- new File(reportHome).listFiles(isAFileWithUserNameMatching(holdingPattern))
    ) {
      val fileName = aFile.getName
      val holdingPattern(userName) = fileName
      val holdings = FileHoldingFactory(userName, s"$reportHome/$fileName").getHoldings()

      println(s"Persisting holdings for user $userName, file $fileName")
      HoldingsDatabase().insert(holdings)
    }
  }

  def reloadTransactions() = {
    val transactionPattern =
      """txs.*_([a-zA-Z]+)\.txt""".r
    for (
      aFile <- new File(reportHome).listFiles(isAFileWithUserNameMatching(transactionPattern))
    ) {
      val fileName = aFile.getName
      val transactionPattern(userName) = fileName
      val transactions = FileTransactionFactory(userName, s"$reportHome/$fileName").getTransactions()

      println(s"Persisting transactions for user $userName, file $fileName")
      Transaction.insert(transactions)
    }
  }

  def reloadPrices() = {
    val pricePattern = """prices.*\.txt""".r
    for (
      aFile <- new File(reportHome).listFiles(isAFileWithoutUserNameMatching(pricePattern))
    ) {
      val fileName = aFile.getName
      println(s"Persisting prices, file $fileName")
      PricesDatabase().insert(FilePriceFactory(s"$reportHome/$fileName").getPrices())
    }
  }

  private val application = FakeApplication()

  Play.start(application)

  deleteAllRows()

  // Give transaction prices precedence over holdings for calculation purposes
  reloadTransactions()
  reloadHoldings()
  reloadPrices()

  Play.stop(application)
}