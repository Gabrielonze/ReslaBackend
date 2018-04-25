package controllers

import javax.inject._

import models.RequestRepository
import play.api.libs.json.Json
import play.api.mvc._

@Singleton
class RequestController @Inject()(requestDB: RequestRepository) extends Controller with SecurityController {

  def getRequests(bookId: Long) = Action.async { implicit request =>
    check { u =>
      val requests = requestDB.findByBook(bookId)
      FOk(Json.toJson(requests).toString())
    }
  }

}
