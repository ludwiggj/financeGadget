package models.org.ludwiggj.finance.persistence.database

import models.org.ludwiggj.finance.persistence.database.PKs.PK
import org.scalatest.{BeforeAndAfter, DoNotDiscover}
import org.scalatestplus.play.{ConfiguredApp, PlaySpec}
import play.api.Play
import play.api.db.slick.DatabaseConfigProvider
import slick.driver.JdbcProfile

import scala.language.postfixOps

@DoNotDiscover
class UserSpec extends PlaySpec with ConfiguredApp with BeforeAndAfter {

  before {
    TestDatabase.recreateSchema()
  }

  val databaseLayer = new DatabaseLayer(DatabaseConfigProvider.get[JdbcProfile]("financeTest")(Play.current))
  import databaseLayer._

  object SingleUser {
    val username = "Father_Ted"
    val password = Some("Penitent_Man")

    def insert() = {
      exec(Users.insert(username, password))
    }
  }

  "get" should {
    "return empty if user is not present" in {
      exec(Users.get("Burt Bacharach")) must equal(None)
    }

    "return existing user row if it is present" in {
      val userId = SingleUser.insert()

      exec(Users.get(SingleUser.username)) mustBe Some(
        UserRow(userId, SingleUser.username, SingleUser.password)
      )
    }
  }

  "getOrInsert" should {
    "insert user if it is not present" in {
      exec(Users.getOrInsert("bob")) must be > PK[UserTable](0L)
    }

    "return existing user id if user is present" in {
      val userId = SingleUser.insert()

      exec(Users.getOrInsert(SingleUser.username)) must equal(userId)
    }
  }

  "authenticate" should {
    "return 0 if user is not present" in {
      exec(Users.authenticate("dummy user", "blah")) must equal(0)
    }

    "return 1 if user is present and password does not match" in {
      SingleUser.insert()

      exec(Users.authenticate(SingleUser.username, "blah")) must equal(0)
    }

    "return 1 if user is present and password matches" in {
      SingleUser.insert()

      exec(Users.authenticate(SingleUser.username, SingleUser.password.get)) must equal(1)
    }
  }
}