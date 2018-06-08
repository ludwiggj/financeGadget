package utils

import java.util.concurrent.TimeoutException

import com.gargoylesoftware.htmlunit.ElementNotFoundException
import com.github.nscala_time.time.Imports.{DateTime, DateTimeFormat}
import models.org.ludwiggj.finance.domain.{Holding, Price}
import models.org.ludwiggj.finance.persistence.database.DatabaseLayer
import models.org.ludwiggj.finance.persistence.file.FilePersister
import models.org.ludwiggj.finance.web._
import play.api.db.slick.DatabaseConfigProvider
import play.api.inject.guice.GuiceApplicationBuilder
import play.api.{Configuration, Environment, Play}

import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.duration._
import scala.concurrent.{Await, Future}
import scala.language.postfixOps
import scala.util.{Failure, Success}

object WebSiteHoldingScraper extends App {

  lazy val app = new GuiceApplicationBuilder(configuration = Configuration.load(Environment.simple(), Map(
    "config.resource" -> "application.conf"
  ))).build()

  val databaseLayer = new DatabaseLayer(app.injector.instanceOf[DatabaseConfigProvider].get)

  import databaseLayer._

  private val config = WebSiteConfig("acme")
  private val loginForm = LoginForm(config, targetPage = "valuations")
  private val users = config.getUserList()

  def getHoldings(user: User) = Future[(User, List[Holding])] {
    def getHoldings(): List[Holding] = {
      WebSiteHoldingFactory(loginForm, user.name).getHoldings() map {
        holding => holding.copy(userName = user.reportName)
      }
    }

    val userName = user.name
    val emptyResult = (user, List())

    time(s"processHoldings($userName)",
      try {
        val holdings = getHoldings()

        val holdingsTotal = holdings.map(h => h.value).sum
        println(s"Total holdings ($userName): £$holdingsTotal")

        (user, holdings)
      } catch {
        case ex: NotAuthenticatedException =>
          println(s"Cannot retrieve holdings for $userName [NotAuthenticatedException]. Error ${ex.toString}")
          emptyResult

        case ex: ElementNotFoundException =>
          println(s"Cannot retrieve holdings for $userName [ElementNotFoundException]. Error ${ex.toString}")
          emptyResult
      }
    )
  }

  def scrape() = {
    def processHoldings(listOfUserHoldings: List[(User, List[Holding])]) = {
      val date = DateTime.now.toString(DateTimeFormat.forPattern("yy_MM_dd"))

      def persistHoldingsToFile(userReportName: String, holdings: List[Holding]): Unit = {
        FilePersister(fileName = s"$holdingsHome/holdings_${date}_${userReportName}.txt").write(holdings)
      }

      def persistPricesToFile(userReportName: String, prices: List[Price]): Unit = {
        FilePersister(fileName = s"$pricesHome/prices_${date}_${userReportName}.txt").write(prices)
      }

      for {
        (user, holdings) <- listOfUserHoldings
      } {
        persistHoldingsToFile(user.reportName, holdings)

        val prices = holdings.map(_.price)
        persistPricesToFile(user.reportName, prices)
        exec(Prices.insert(prices))
      }
    }

    def displayTotalHoldingAmount(listOfUserHoldings: List[(User, List[Holding])]) = {
      val allHoldings = listOfUserHoldings.map(_._2).flatten
      val totalHoldingAmount = allHoldings.map(h => h.value).sum
      println(s"Total £$totalHoldingAmount")
    }

    // scrape(): start here
    time("Whole thing",
      try {
        val listOfFutures = users map { user =>
          getHoldings(user)
        }

        val combinedFuture = Future.sequence(listOfFutures)

        Await.ready(combinedFuture, 30 seconds).value.get match {
          case Success(listOfUserHoldings) =>
            processHoldings(listOfUserHoldings)
            displayTotalHoldingAmount(listOfUserHoldings)
            println(s"Done...")

          case Failure(ex) =>
            println(s"Oh dear... ${ex.getMessage}")
            ex.printStackTrace()
        }
      } catch {
        case ex: TimeoutException => println(ex.getMessage)
      }
    )
  }

  try {
    Play.start(app)
    scrape()
  } finally {
    Play.stop(app)

    // Kludge to force the app to terminate
    System.exit(0)
  }
}