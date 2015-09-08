package models.org.ludwiggj.finance.web

import models.org.ludwiggj.finance.builders.LoginFormBuilder
import models.org.ludwiggj.finance.domain.Transaction

class WebSiteTransactionFactory(private val loginFormBuilder: LoginFormBuilder, private val accountName: String)
  extends {
    val financeEntityTableSelector = s"table[id~=dgTransactions] tr"
  } with HtmlPageFinanceRowParser {

  def getTransactions(): List[Transaction] = {
    val loginForm = loginFormBuilder.loggingIntoPage("transactions").build()
    val loggedInPage = loginForm.loginAs(accountName)
    val txRows = parseRows(loggedInPage)
    loggedInPage.logOff()
    (for (txRow <- txRows) yield Transaction(txRow.toString)).toList
  }
}

object WebSiteTransactionFactory {
  def apply(loginFormBuilder: LoginFormBuilder, accountName: String) = {
    new WebSiteTransactionFactory(loginFormBuilder, accountName)
  }
}