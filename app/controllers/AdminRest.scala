package controllers

import javax.inject._
import models._
import play.api.data.Form
import play.api.data.Forms.{ mapping, optional, text }
import play.api.libs.json.Json
import play.api.mvc._
import play.api.data.Forms._

case class DishSave(dishId: Option[Long], restaurantId: Long, name: String, description: String, imageUrl: String, price: BigDecimal, category: String)

@Singleton
class AdminRest @Inject() (requestDB: RequestRepository, dishDB: DishRepository, bookDB: BookRepository) extends Controller with SecurityController {

  val saveDishForm = Form(
    mapping(
      "dishId" -> optional(longNumber),
      "restaurantId" -> longNumber,
      "name" -> nonEmptyText,
      "description" -> nonEmptyText,
      "imageUrl" -> nonEmptyText,
      "price" -> bigDecimal,
      "category" -> nonEmptyText
    )(DishSave.apply)(DishSave.unapply)
  )

  def getBooks(restaurantId: Long) = Action.async { implicit request =>
    check { u =>
      val books = bookDB.findByRestaurant(restaurantId).map { b =>
        val req = requestDB.findByBook(b.bookId.get)
        (b, req)
      }
      FOk("Json.toJson(books).toString()")
    }
  }

  def getDishes(restaurantId: Long) = Action.async { implicit request =>
    check { u =>
      val dishes = dishDB.findByRestaurant(restaurantId)
      FOk(views.html.dishes(dishes))
    }
  }

  def getDish(dishId: Long) = Action.async { implicit request =>
    check { u =>
      val dish = dishDB.findByid(dishId).get
      FOk("Json.toJson(dish).toString()")
    }
  }

  def saveDish() = Action.async { implicit request =>
    check { u =>
      saveDishForm.bindFromRequest.fold(
        formWithErrors => {
          val error = formWithErrors.errors.toString()
          print(s"\n\nsaveBook: ${error}\n\n")
          FBadRequest(error)
        },
        pi => {
          pi.dishId match {
            case Some(a) => dishDB.update(Dish(pi.dishId, pi.restaurantId, pi.name, pi.description, pi.imageUrl, pi.price, pi.category))
            case _       => dishDB.insert(Dish(None, pi.restaurantId, pi.name, pi.description, pi.imageUrl, pi.price, pi.category))
          }

          FOk(Json.obj("ok" -> true).toString)
        }
      )
    }
  }

}
