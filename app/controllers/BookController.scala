package controllers

import javax.inject._

import models.BookRepository
import play.api.libs.json.Json
import play.api.mvc._

@Singleton
class BookController @Inject() (bookDB: BookRepository) extends Controller with SecurityController {

  def getBooks = Action.async { implicit request =>
    check { u =>
      val books = bookDB.findByCustumer(u.custumerId.get)
      FOk(Json.toJson(books).toString())
    }
  }

}
