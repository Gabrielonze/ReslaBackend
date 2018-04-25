package controllers

import javax.inject._

import models._
import play.api.libs.json.Json
import play.api.mvc._

case class RestaurantGrade(restaurantId: Option[Long], name: String, address: String, grade: Option[BigDecimal])
case class DishGrade(dishId: Option[Long], restaurantId: Long, name: String, description: String, imageUrl: String, grade: Option[BigDecimal])

@Singleton
class RestaurantController @Inject() (
    restaurantDB: RestaurantRepository,
    bookDB: BookRepository,
    dishDB: DishRepository,
    requestDB: RequestRepository
) extends Controller with SecurityController {

  implicit val restaurantGradeFormat = Json.format[RestaurantGrade]
  implicit val dishGradeFormat = Json.format[DishGrade]

  def getRestaurants = Action.async { implicit request =>
    check { u =>
      val restaurants = restaurantDB.findAll().map { r =>
        val grade = bookDB.findRating(r.restaurantId.get)
        RestaurantGrade(r.restaurantId, r.name, r.address, grade)
      }
      FOk(Json.toJson(restaurants).toString())
    }
  }

  def getDishes(restaurantId: Long) = Action.async { implicit request =>
    check { u =>
      val dishes = dishDB.findAll().map { r =>
        val grade = requestDB.findRating(r.dishId.get)
        DishGrade(r.dishId, r.restaurantId, r.name, r.description, r.imageUrl, grade)
      }
      FOk(Json.toJson(dishes).toString())
    }
  }
}
