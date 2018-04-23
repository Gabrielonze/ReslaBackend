package controllers

import play.api.http.HeaderNames
import play.api.mvc.{ Action, Request, Result }
import play.api.mvc.Results._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.concurrent.Future

case class CorsAction[A](action: Action[A]) extends Action[A] {

  def apply(request: Request[A]): Future[Result] = {
    action(request).map(result => result.withHeaders(
      HeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN -> "*",
      HeaderNames.ALLOW -> "*",
      HeaderNames.ACCESS_CONTROL_ALLOW_METHODS -> "POST, GET, PUT, DELETE, OPTIONS",
      HeaderNames.ACCESS_CONTROL_ALLOW_HEADERS -> "Content-Type, Accept, ACCESS_CONTROL_ALLOW_ORIGIN, User-Agent, X-Auth-Token"
    ))
  }

  lazy val parser = action.parser
}

trait SecurityController {

  def FOk[A](res: A)(implicit writeable: play.api.http.Writeable[A]) = {
    Future(DOk(res))
  }

  def DOk[A](res: A)(implicit writeable: play.api.http.Writeable[A]) = {
    play.api.mvc.Results.Ok(res).withHeaders(
      HeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN -> "*",
      HeaderNames.ALLOW -> "*",
      HeaderNames.ACCESS_CONTROL_ALLOW_METHODS -> "POST, GET, PUT, DELETE, OPTIONS",
      HeaderNames.ACCESS_CONTROL_ALLOW_HEADERS -> "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent, X-Auth-Token"
    )
  }

  def DUnauthorized[A](res: A)(implicit writeable: play.api.http.Writeable[A]) = {
    play.api.mvc.Results.Unauthorized(res).withHeaders(
      HeaderNames.ACCESS_CONTROL_ALLOW_ORIGIN -> "*",
      HeaderNames.ALLOW -> "*",
      HeaderNames.ACCESS_CONTROL_ALLOW_METHODS -> "POST, GET, PUT, DELETE, OPTIONS",
      HeaderNames.ACCESS_CONTROL_ALLOW_HEADERS -> "Origin, X-Requested-With, Content-Type, Accept, Referer, User-Agent, X-Auth-Token"
    )
  }

  def FBadRequest(res: String) = {
    Future(BadRequest(res))
  }

  def FRedirect(res: String) = {
    Future(Redirect(res))
  }

  def FUnauthorized(res: String) = {
    Future(Unauthorized(res))
  }

}
