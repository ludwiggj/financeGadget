package models.org.ludwiggj.finance.web

import com.gargoylesoftware.htmlunit.html.{HtmlForm, HtmlInput, HtmlSubmitInput}
import play.api.Logger

class LoginForm private(private val baseUrl: String,
                        private val fields: List[FormField],
                        private val submitButton: String,
                        private val users: List[User],
                        private val logoutText: String
                       ) extends Login {

  private def userByName: Map[String, User] = Map((for (user <- users) yield (user.name, user)): _*)

  def loginAs(userName: String): HtmlEntity = {
    val user: User = userByName(userName)
    val page: HtmlEntity = WebClient.getPage(baseUrl)
    val form: HtmlForm = page.getForms.head

    fields foreach {
      f => form.getInputByName(f.htmlName).asInstanceOf[HtmlInput].setValueAttribute(user.attributeValue(f.name))
    }

    Logger.info(s"Logging in to $baseUrl as ${user.name}")

    HtmlPage(form.getInputByName(submitButton).asInstanceOf[HtmlSubmitInput].click(), logoutText)
  }
}

object LoginForm {
  def apply(config: WebSiteConfig, targetPage: String) = {
    new LoginForm(
      config.getUrlForPage(targetPage),
      config.getLoginFormFields(),
      config.getSubmitButton(),
      config.getUserList(),
      config.getLogoutText()
    )
  }
}