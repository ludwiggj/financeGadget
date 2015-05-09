package org.ludwiggj.finance.persistence.database

// AUTO-GENERATED Slick data model

/** Stand-alone Slick data model for immediate use */
object Tables extends {
  val profile = scala.slick.driver.MySQLDriver
} with Tables

/** Slick data model trait for extension, choice of backend or usage in the cake pattern. (Make sure to initialize this late.) */
trait Tables {

  // Graeme, added to handle BigDecimal bug
  import scala.language.implicitConversions
  implicit def string2BigDecimal(value: String) = new scala.math.BigDecimal(new java.math.BigDecimal(value))

  val profile: scala.slick.driver.JdbcProfile
  import profile.simple._

  import scala.slick.model.ForeignKeyAction
  // NOTE: GetResult mappers for plain SQL are only generated for tables where Slick knows how to map the types of all columns.
  import scala.slick.jdbc.{GetResult => GR}

  /** DDL for all tables. Call .create to execute. */
  lazy val ddl = Changelog.ddl ++ Funds.ddl ++ Holdings.ddl ++ Prices.ddl ++ Transactions.ddl ++ Users.ddl

  /** Entity class storing rows of table Changelog
   *  @param changeNumber Database column change_number DBType(BIGINT), PrimaryKey
   *  @param completeDt Database column complete_dt DBType(TIMESTAMP)
   *  @param appliedBy Database column applied_by DBType(VARCHAR), Length(100,true)
   *  @param description Database column description DBType(VARCHAR), Length(500,true) */
  case class ChangelogRow(changeNumber: Long, completeDt: java.sql.Timestamp, appliedBy: String, description: String)
  /** GetResult implicit for fetching ChangelogRow objects using plain SQL queries */
  implicit def GetResultChangelogRow(implicit e0: GR[Long], e1: GR[java.sql.Timestamp], e2: GR[String]): GR[ChangelogRow] = GR{
    prs => import prs._
    ChangelogRow.tupled((<<[Long], <<[java.sql.Timestamp], <<[String], <<[String]))
  }
  /** Table description of table changelog. Objects of this class serve as prototypes for rows in queries. */
  class Changelog(_tableTag: Tag) extends Table[ChangelogRow](_tableTag, "changelog") {
    def * = (changeNumber, completeDt, appliedBy, description) <> (ChangelogRow.tupled, ChangelogRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (changeNumber.?, completeDt.?, appliedBy.?, description.?).shaped.<>({r=>import r._; _1.map(_=> ChangelogRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column change_number DBType(BIGINT), PrimaryKey */
    val changeNumber: Column[Long] = column[Long]("change_number", O.PrimaryKey)
    /** Database column complete_dt DBType(TIMESTAMP) */
    val completeDt: Column[java.sql.Timestamp] = column[java.sql.Timestamp]("complete_dt")
    /** Database column applied_by DBType(VARCHAR), Length(100,true) */
    val appliedBy: Column[String] = column[String]("applied_by", O.Length(100,varying=true))
    /** Database column description DBType(VARCHAR), Length(500,true) */
    val description: Column[String] = column[String]("description", O.Length(500,varying=true))
  }
  /** Collection-like TableQuery object for table Changelog */
  lazy val Changelog = new TableQuery(tag => new Changelog(tag))

  /** Entity class storing rows of table Funds
   *  @param id Database column ID DBType(BIGINT), AutoInc, PrimaryKey
   *  @param name Database column NAME DBType(VARCHAR), Length(254,true) */
  case class FundsRow(id: Long, name: String)
  /** GetResult implicit for fetching FundsRow objects using plain SQL queries */
  implicit def GetResultFundsRow(implicit e0: GR[Long], e1: GR[String]): GR[FundsRow] = GR{
    prs => import prs._
    FundsRow.tupled((<<[Long], <<[String]))
  }
  /** Table description of table FUNDS. Objects of this class serve as prototypes for rows in queries. */
  class Funds(_tableTag: Tag) extends Table[FundsRow](_tableTag, "FUNDS") {
    def * = (id, name) <> (FundsRow.tupled, FundsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?).shaped.<>({r=>import r._; _1.map(_=> FundsRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column ID DBType(BIGINT), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column NAME DBType(VARCHAR), Length(254,true) */
    val name: Column[String] = column[String]("NAME", O.Length(254,varying=true))

    /** Uniqueness Index over (name) (database name NAME) */
    val index1 = index("NAME", name, unique=true)
  }
  /** Collection-like TableQuery object for table Funds */
  lazy val Funds = new TableQuery(tag => new Funds(tag))

  /** Entity class storing rows of table Holdings
   *  @param fundId Database column FUND_ID DBType(BIGINT)
   *  @param userId Database column USER_ID DBType(BIGINT)
   *  @param units Database column UNITS DBType(DECIMAL)
   *  @param holdingDate Database column HOLDING_DATE DBType(DATE) */
  case class HoldingsRow(fundId: Long, userId: Long, units: scala.math.BigDecimal, holdingDate: java.sql.Date)
  /** GetResult implicit for fetching HoldingsRow objects using plain SQL queries */
  implicit def GetResultHoldingsRow(implicit e0: GR[Long], e1: GR[scala.math.BigDecimal], e2: GR[java.sql.Date]): GR[HoldingsRow] = GR{
    prs => import prs._
    HoldingsRow.tupled((<<[Long], <<[Long], <<[scala.math.BigDecimal], <<[java.sql.Date]))
  }
  /** Table description of table HOLDINGS. Objects of this class serve as prototypes for rows in queries. */
  class Holdings(_tableTag: Tag) extends Table[HoldingsRow](_tableTag, "HOLDINGS") {
    def * = (fundId, userId, units, holdingDate) <> (HoldingsRow.tupled, HoldingsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (fundId.?, userId.?, units.?, holdingDate.?).shaped.<>({r=>import r._; _1.map(_=> HoldingsRow.tupled((_1.get, _2.get, _3.get, _4.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column FUND_ID DBType(BIGINT) */
    val fundId: Column[Long] = column[Long]("FUND_ID")
    /** Database column USER_ID DBType(BIGINT) */
    val userId: Column[Long] = column[Long]("USER_ID")
    /** Database column UNITS DBType(DECIMAL) */
    val units: Column[scala.math.BigDecimal] = column[scala.math.BigDecimal]("UNITS")
    /** Database column HOLDING_DATE DBType(DATE) */
    val holdingDate: Column[java.sql.Date] = column[java.sql.Date]("HOLDING_DATE")

    /** Primary key of Holdings (database name HOLDINGS_PK) */
    val pk = primaryKey("HOLDINGS_PK", (fundId, userId, holdingDate))

    /** Foreign key referencing Funds (database name HOLDINGS_FUND_FK) */
    lazy val fundsFk = foreignKey("HOLDINGS_FUND_FK", fundId, Funds)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Prices (database name HOLDINGS_PRICES_FK) */
    lazy val pricesFk = foreignKey("HOLDINGS_PRICES_FK", (fundId, holdingDate), Prices)(r => (r.fundId, r.priceDate), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name HOLDINGS_USERS_FK) */
    lazy val usersFk = foreignKey("HOLDINGS_USERS_FK", userId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Holdings */
  lazy val Holdings = new TableQuery(tag => new Holdings(tag))

  /** Entity class storing rows of table Prices
   *  @param fundId Database column FUND_ID DBType(BIGINT)
   *  @param priceDate Database column PRICE_DATE DBType(DATE)
   *  @param price Database column PRICE DBType(DECIMAL) */
  case class PricesRow(fundId: Long, priceDate: java.sql.Date, price: scala.math.BigDecimal)
  /** GetResult implicit for fetching PricesRow objects using plain SQL queries */
  implicit def GetResultPricesRow(implicit e0: GR[Long], e1: GR[java.sql.Date], e2: GR[scala.math.BigDecimal]): GR[PricesRow] = GR{
    prs => import prs._
    PricesRow.tupled((<<[Long], <<[java.sql.Date], <<[scala.math.BigDecimal]))
  }
  /** Table description of table PRICES. Objects of this class serve as prototypes for rows in queries. */
  class Prices(_tableTag: Tag) extends Table[PricesRow](_tableTag, "PRICES") {
    def * = (fundId, priceDate, price) <> (PricesRow.tupled, PricesRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (fundId.?, priceDate.?, price.?).shaped.<>({r=>import r._; _1.map(_=> PricesRow.tupled((_1.get, _2.get, _3.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column FUND_ID DBType(BIGINT) */
    val fundId: Column[Long] = column[Long]("FUND_ID")
    /** Database column PRICE_DATE DBType(DATE) */
    val priceDate: Column[java.sql.Date] = column[java.sql.Date]("PRICE_DATE")
    /** Database column PRICE DBType(DECIMAL) */
    val price: Column[scala.math.BigDecimal] = column[scala.math.BigDecimal]("PRICE")

    /** Primary key of Prices (database name PRICES_PK) */
    val pk = primaryKey("PRICES_PK", (fundId, priceDate))

    /** Foreign key referencing Funds (database name PRICES_FUNDS_FK) */
    lazy val fundsFk = foreignKey("PRICES_FUNDS_FK", fundId, Funds)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Prices */
  lazy val Prices = new TableQuery(tag => new Prices(tag))

  /** Entity class storing rows of table Transactions
   *  @param fundId Database column FUND_ID DBType(BIGINT)
   *  @param userId Database column USER_ID DBType(BIGINT)
   *  @param transactionDate Database column TRANSACTION_DATE DBType(DATE)
   *  @param description Database column DESCRIPTION DBType(VARCHAR), Length(254,true)
   *  @param amountIn Database column AMOUNT_IN DBType(DECIMAL), Default(0.0000)
   *  @param amountOut Database column AMOUNT_OUT DBType(DECIMAL), Default(0.0000)
   *  @param priceDate Database column PRICE_DATE DBType(DATE)
   *  @param units Database column UNITS DBType(DECIMAL) */
  case class TransactionsRow(fundId: Long, userId: Long, transactionDate: java.sql.Date, description: String, amountIn: scala.math.BigDecimal = "0.0000", amountOut: scala.math.BigDecimal = "0.0000", priceDate: java.sql.Date, units: scala.math.BigDecimal)
  /** GetResult implicit for fetching TransactionsRow objects using plain SQL queries */
  implicit def GetResultTransactionsRow(implicit e0: GR[Long], e1: GR[java.sql.Date], e2: GR[String], e3: GR[scala.math.BigDecimal]): GR[TransactionsRow] = GR{
    prs => import prs._
    TransactionsRow.tupled((<<[Long], <<[Long], <<[java.sql.Date], <<[String], <<[scala.math.BigDecimal], <<[scala.math.BigDecimal], <<[java.sql.Date], <<[scala.math.BigDecimal]))
  }
  /** Table description of table TRANSACTIONS. Objects of this class serve as prototypes for rows in queries. */
  class Transactions(_tableTag: Tag) extends Table[TransactionsRow](_tableTag, "TRANSACTIONS") {
    def * = (fundId, userId, transactionDate, description, amountIn, amountOut, priceDate, units) <> (TransactionsRow.tupled, TransactionsRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (fundId.?, userId.?, transactionDate.?, description.?, amountIn.?, amountOut.?, priceDate.?, units.?).shaped.<>({r=>import r._; _1.map(_=> TransactionsRow.tupled((_1.get, _2.get, _3.get, _4.get, _5.get, _6.get, _7.get, _8.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column FUND_ID DBType(BIGINT) */
    val fundId: Column[Long] = column[Long]("FUND_ID")
    /** Database column USER_ID DBType(BIGINT) */
    val userId: Column[Long] = column[Long]("USER_ID")
    /** Database column TRANSACTION_DATE DBType(DATE) */
    val transactionDate: Column[java.sql.Date] = column[java.sql.Date]("TRANSACTION_DATE")
    /** Database column DESCRIPTION DBType(VARCHAR), Length(254,true) */
    val description: Column[String] = column[String]("DESCRIPTION", O.Length(254,varying=true))
    /** Database column AMOUNT_IN DBType(DECIMAL), Default(0.0000) */
    val amountIn: Column[scala.math.BigDecimal] = column[scala.math.BigDecimal]("AMOUNT_IN", O.Default("0.0000"))
    /** Database column AMOUNT_OUT DBType(DECIMAL), Default(0.0000) */
    val amountOut: Column[scala.math.BigDecimal] = column[scala.math.BigDecimal]("AMOUNT_OUT", O.Default("0.0000"))
    /** Database column PRICE_DATE DBType(DATE) */
    val priceDate: Column[java.sql.Date] = column[java.sql.Date]("PRICE_DATE")
    /** Database column UNITS DBType(DECIMAL) */
    val units: Column[scala.math.BigDecimal] = column[scala.math.BigDecimal]("UNITS")

    /** Primary key of Transactions (database name TRANSACTIONS_PK) */
    val pk = primaryKey("TRANSACTIONS_PK", (fundId, userId, transactionDate, description, amountIn, amountOut, priceDate, units))

    /** Foreign key referencing Funds (database name TRANSACTIONS_FUNDS_FK) */
    lazy val fundsFk = foreignKey("TRANSACTIONS_FUNDS_FK", fundId, Funds)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Prices (database name TRANSACTIONS_PRICES_FK) */
    lazy val pricesFk = foreignKey("TRANSACTIONS_PRICES_FK", (fundId, priceDate), Prices)(r => (r.fundId, r.priceDate), onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
    /** Foreign key referencing Users (database name TRANSACTIONS_USERS_FK) */
    lazy val usersFk = foreignKey("TRANSACTIONS_USERS_FK", userId, Users)(r => r.id, onUpdate=ForeignKeyAction.NoAction, onDelete=ForeignKeyAction.NoAction)
  }
  /** Collection-like TableQuery object for table Transactions */
  lazy val Transactions = new TableQuery(tag => new Transactions(tag))

  /** Entity class storing rows of table Users
   *  @param id Database column ID DBType(BIGINT), AutoInc, PrimaryKey
   *  @param name Database column NAME DBType(VARCHAR), Length(254,true) */
  case class UsersRow(id: Long, name: String)
  /** GetResult implicit for fetching UsersRow objects using plain SQL queries */
  implicit def GetResultUsersRow(implicit e0: GR[Long], e1: GR[String]): GR[UsersRow] = GR{
    prs => import prs._
    UsersRow.tupled((<<[Long], <<[String]))
  }
  /** Table description of table USERS. Objects of this class serve as prototypes for rows in queries. */
  class Users(_tableTag: Tag) extends Table[UsersRow](_tableTag, "USERS") {
    def * = (id, name) <> (UsersRow.tupled, UsersRow.unapply)
    /** Maps whole row to an option. Useful for outer joins. */
    def ? = (id.?, name.?).shaped.<>({r=>import r._; _1.map(_=> UsersRow.tupled((_1.get, _2.get)))}, (_:Any) =>  throw new Exception("Inserting into ? projection not supported."))

    /** Database column ID DBType(BIGINT), AutoInc, PrimaryKey */
    val id: Column[Long] = column[Long]("ID", O.AutoInc, O.PrimaryKey)
    /** Database column NAME DBType(VARCHAR), Length(254,true) */
    val name: Column[String] = column[String]("NAME", O.Length(254,varying=true))

    /** Uniqueness Index over (name) (database name NAME) */
    val index1 = index("NAME", name, unique=true)
  }
  /** Collection-like TableQuery object for table Users */
  lazy val Users = new TableQuery(tag => new Users(tag))
}