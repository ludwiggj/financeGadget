package controllers

import java.sql.Date
import java.util
import models.org.ludwiggj.finance.persistence.database.TransactionsDatabase
import org.joda.time.DateTime
import play.api.mvc._
import models.org.ludwiggj.finance.domain.{FinanceDate, CashDelta}
import models.org.ludwiggj.finance.Portfolio
import models.org.ludwiggj.finance.domain.FinanceDate.sqlDateToFinanceDate
import models.org.ludwiggj.finance.dateTimeToDate
import play.api.cache._
import javax.inject.Inject
import play.api.Play.current

import scala.collection.immutable.SortedMap

class Portfolios @Inject()(cache: CacheApi) extends Controller {

  private def getPortfolioDataOnDate(date: Date): List[Portfolio] = {
    cache.getOrElse[List[Portfolio]](s"portfolios-$date") {
      Portfolio.get(date)
    }
  }

  def onDate(date: Date) = Cached(s"portfolioDataOnDate-$date") {
    Action {
      implicit request =>
        val portfolios = getPortfolioDataOnDate(date)

        val grandTotal = portfolios.foldRight(CashDelta(0, 0))(
          (portfolio, delta) => delta.add(portfolio.delta)
        )

        Ok(views.html.portfolios.details(portfolios, date, grandTotal))
    }
  }

  private def yearsAgoCacheKey(prefix: String) = { (header: RequestHeader) =>
    prefix + header.getQueryString("yearsAgo").getOrElse("0")
  }

  def all(numberOfYearsAgoOption: Option[Int]) = Cached(yearsAgoCacheKey("portfolioDataAll-yearsAgo-")) {
    Action {
      implicit request =>
        val transactionsDatabase = TransactionsDatabase()

        val investmentDates = cache.getOrElse[List[FinanceDate]]("investmentDates") {
          transactionsDatabase.getRegularInvestmentDates().map(sqlDateToFinanceDate)
        }

        def investmentDatesFromAPreviousYear(numberOfYearsAgoOfInterest: Int) = {
          val now = new DateTime()
          val beforeDate: util.Date = now.minusYears(numberOfYearsAgoOfInterest)
          val afterDate: util.Date = now.minusYears(numberOfYearsAgoOfInterest + 1)

          investmentDates.filter { d =>
            d.after(afterDate) && d.before(beforeDate)
          }
        }

        def earlierDataIsAvailable(numberOfYearsAgoOfInterest: Int) = {
          !investmentDatesFromAPreviousYear(numberOfYearsAgoOfInterest).isEmpty
        }

        def getPortfolios(investmentDatesOfInterest: List[FinanceDate]): Map[FinanceDate, (List[Portfolio], CashDelta)] = {
          def getInvestmentDates(): List[FinanceDate] = {
            val latestDates = transactionsDatabase.getTransactionsDatesSince(investmentDates.head).map(sqlDateToFinanceDate)
            latestDates ++ investmentDatesOfInterest
          }

          val portfolios = (getInvestmentDates() map (date => {
            val thePortfolios = getPortfolioDataOnDate(date)

            val grandTotal = thePortfolios.foldRight(CashDelta(0, 0))(
              (portfolio, delta) => delta.add(portfolio.delta)
            )

            (date, (thePortfolios, grandTotal))
          }))

          implicit val Ord = implicitly[Ordering[FinanceDate]]
          SortedMap(portfolios: _*)(Ord.reverse)
        }

        val numberOfYearsAgo = numberOfYearsAgoOption.getOrElse(0)
        val investmentDatesOfInterest = investmentDatesFromAPreviousYear(numberOfYearsAgo)

        if (investmentDatesOfInterest.isEmpty) {
          BadRequest(s"This was a bad request: yearsAgo=$numberOfYearsAgo")
        } else {
          val oneYearEarlier = numberOfYearsAgo + 1
          val previousYear = if (earlierDataIsAvailable(oneYearEarlier)) Some(oneYearEarlier) else None

          val oneYearLater = numberOfYearsAgo - 1
          val nextYear = if (oneYearLater >= 0) Some(oneYearLater) else None

          val portfolios = getPortfolios(investmentDatesOfInterest)

          Ok(views.html.portfolios.all(portfolios, previousYear, nextYear))
        }
    }
  }
}