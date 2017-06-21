package models.org.ludwiggj.finance.persistence.database

import models.org.ludwiggj.finance.aLocalDate
import models.org.ludwiggj.finance.domain.{Fund, FundName, Price}
import models.org.ludwiggj.finance.persistence.database.Tables.FundRow
import org.scalatest.{BeforeAndAfter, DoNotDiscover, Inside}
import org.scalatestplus.play.{ConfiguredApp, PlaySpec}

@DoNotDiscover
class PriceSpec extends PlaySpec with DatabaseHelpers with ConfiguredApp with BeforeAndAfter with Inside {

  before {
    TestDatabase.recreateSchema()
  }

  "get a single price" should {
    "return empty if price is not present" in {
      EmptySchema.loadData()

      Price.get(FundName("fund that is not present"), aLocalDate("20/05/2014")) must equal(None)
    }

    "return existing price if it is present" in {
      SinglePrice.loadData()

      Price.get(price("kappa140520").fundName, price("kappa140520").date) mustBe Some(price("kappa140520"))
    }
  }

  "get a list of prices" should {
    "return the list" in {
      TwoPrices.loadData()

      Price.get() must contain theSameElementsAs List(price("kappa140520"), price("nike140620"))
    }

    "be unchanged if attempt to add price for same date" in {
      TwoPrices.loadData()

      Price.insert(price("kappa140520").copy(inPounds = 2.12))
      Price.get() must contain theSameElementsAs List(price("kappa140520"), price("nike140620"))
    }

    "increase by one in length if add new unique price" in {
      TwoPrices.loadData()

      val newPrice = Price("holding1", "21/05/2014", 2.12)
      Price.insert(newPrice)
      Price.get() must contain theSameElementsAs List(price("kappa140520"), price("nike140620"), newPrice)
    }
  }

  "insert price" should {
    "insert fund if it is not present" in {
      EmptySchema.loadData()

      val newFundName = FundName("NewFund")

      Fund.get(newFundName) mustBe None

      val capitalistsDreamFundPriceDate = aLocalDate("20/05/2014")
      val price = Price(newFundName, capitalistsDreamFundPriceDate, 1.2)

      Price.insert(price)

      inside(Fund.get(newFundName).get) { case FundRow(_, name) =>
        name must equal(newFundName)
      }

      Price.get(newFundName, capitalistsDreamFundPriceDate) mustBe Some(price)
    }
  }

  "latestPrices" should {
    "return the latest price for each fund" in {
      MultiplePricesForTwoFunds.loadData()

      Price.latestPrices(aLocalDate("20/06/2014")).values.toList must contain theSameElementsAs
        List(price("kappa140523"), price("nike140621"))
    }

    "omit a price if it is zero" in {
      MultiplePricesForSingleFund.loadData()

      Price.latestPrices(aLocalDate("16/05/2014")).values.toList must contain theSameElementsAs
        List(price("kappa140512"))
    }

    "omit a price if it is a two or more days too late" in {
      MultiplePricesForTwoFunds.loadData()

      Price.latestPrices(aLocalDate("19/06/2014")).values.toList must contain theSameElementsAs
        List(price("kappa140523"), price("nike140620"))
    }

    "omit a fund if its earliest price is too late" in {
      MultiplePricesForTwoFunds.loadData()

      Price.latestPrices(aLocalDate("21/05/2014")).values.toList must contain theSameElementsAs
        List(price("kappa140520"))

    }

    "omit prices from name change fund if date of interest is more than one day before the fund change date" in {
      MultiplePricesForSingleFundAndItsRenamedEquivalent.loadData()

      Price.latestPrices(aLocalDate("22/05/2014")).values.toList must contain theSameElementsAs
        List(price("kappa140523"))
    }

    "include prices from name change fund if date of interest is one day before the fund change date" in {
      MultiplePricesForSingleFundAndItsRenamedEquivalent.loadData()

      val expectedUpdatedPrice = price("kappaII140524").copy(fundName = FundName("Kappa"))

      Price.latestPrices(aLocalDate("23/05/2014")).values.toList must contain theSameElementsAs
        List(expectedUpdatedPrice, price("kappaII140524"))
    }

    "include prices from name change fund if date of interest is the fund change date" in {
      MultiplePricesForSingleFundAndItsRenamedEquivalent.loadData()
      val expectedUpdatedPrice = price("kappaII140524").copy(fundName = FundName("Kappa"))

      Price.latestPrices(aLocalDate("24/05/2014")).values.toList must contain theSameElementsAs
        List(expectedUpdatedPrice, price("kappaII140524"))
    }
  }
}