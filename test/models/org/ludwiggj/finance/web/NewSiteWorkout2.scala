package models.org.ludwiggj.finance.web

import java.net.URL
import scala.collection.JavaConverters._
import com.gargoylesoftware.htmlunit.html.HtmlInput
import com.gargoylesoftware.htmlunit.{HttpMethod, WebRequest}
import com.typesafe.config.ConfigFactory
import io.circe.literal._
import io.circe.optics.JsonPath.root
import io.circe.parser.parse
import io.circe.{Decoder, Encoder, HCursor, Json}
import models.org.ludwiggj.finance.domain.{FundName, Holding, Price}
import org.joda.time.LocalDate
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter}

object NewSiteWorkout2 {
  private val config = ConfigFactory.load("acme2")
  private val baseUrl = urlConfigItem("base")
  private val loginUrl = url("login")
  private val logoutUrl = url("logout")

  def urlConfigItem(configItem: String): String = {
    config.getString("site.url." + configItem)
  }

  def url(configItem: String): String = {
    s"$baseUrl${urlConfigItem(configItem)}"
  }

  def login(username: String, password: String): Unit = {
    val html = WebClient.getPage(loginUrl)
    val form = html.getForms.head
    form.getInputByName("username").asInstanceOf[HtmlInput].setValueAttribute(username)
    form.getInputByName("password").asInstanceOf[HtmlInput].setValueAttribute(password)
    form.getElementsByTagName("button").get(0).click()
  }

  def logout(): Unit = {
    WebClient.getPage(loginUrl)
  }

  def dataByPost(url: String): String = {
    val wr = new WebRequest(new URL(url), HttpMethod.POST)
    wr.setAdditionalHeader("Content-Type", "application/json; charset=utf-8")
    wr.setAdditionalHeader("Accept", "application/json, text/javascript, */*; q=0.01")
    WebClient.getPage(wr)
  }

  def holdings(userId: String): Json = {
    val holdings = dataByPost(s"${url("holdings")}$userId")
    parse(holdings).getOrElse((Json.Null))
  }

  def transactions(userId: String): String = {
    dataByPost(s"${url("transactions")}$userId")
  }

  def jsonToHoldings(username: String, json: Json): List[Holding] = {
    val dtf: DateTimeFormatter = DateTimeFormat.forPattern("dd MMMM yyyy")
    val datePath = root._data._data.formattedAsOfDate.string
    val maybeDate = datePath.getOption(json)
    val date = maybeDate.map(dtf.parseLocalDate(_)).getOrElse(LocalDate.now())

    implicit val decodeHold: Decoder[Holding] = (c: HCursor) => for {
      name <- c.downField("name").as[String]
      units <- c.downField("units").as[String]
      price <- c.downField("price").downField("raw").as[BigDecimal]
    } yield {
      Holding(username, Price(FundName(name), date, price), BigDecimal(units.replaceAll(",", "")))
    }

    // Dummy Encoder - required for compilation, though I don't actually want to encode Holdings into json
    implicit val encodeTx: Encoder[Holding] = new Encoder[Holding] {
      final def apply(h: Holding): Json = Json.obj()
    }

    root._data._data.details.each._data.as[Holding].getAll(json)
  }

  def main(args: Array[String]): Unit = {
    val users = (config.getConfigList("site.userAccounts").asScala map (User(_))).toList

    for (user <- users) {
      login(user.username, user.password)
      println("Holdings:")
      val holdingsJson = holdings(user.accountId)
      println(holdingsJson)
      jsonToHoldings(user.name, holdingsJson).foreach(println)
      println(s"Txs:\n${transactions(user.accountId)}")
      logout()
    }
  }
}