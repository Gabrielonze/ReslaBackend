package controllers

import javax.inject._
import models._
import org.joda.time.DateTime
import play.api.libs.json.Json
import play.api.mvc._
import play.api.data.Form
import play.api.data.Forms._

case class DishGrade(id: Option[Long], name: String, description: String, imageUrl: String, price: BigDecimal, quantity: Int, category: String, rating: Option[BigDecimal])
case class RestaurantGrade(id: Option[Long], name: String, address: String, imageUrl: String, rating: Option[BigDecimal])
case class Books(id: Option[Long], name: String, imageUrl: String, used: Boolean, people: Int)
case class FirstScreenResponse(restaurants: List[RestaurantGrade], books: List[Books])
case class SaveNewBook(restautantId: Long, peopleAmount: Int, day: String, observation: Option[String])

@Singleton
class RestaurantController @Inject() (
    restaurantDB: RestaurantRepository,
    bookDB: BookRepository,
    dishDB: DishRepository,
    requestDB: RequestRepository
) extends Controller with SecurityController {

  implicit val restaurantGradeFormat = Json.format[RestaurantGrade]
  implicit val dishGradeFormat = Json.format[DishGrade]
  implicit val booksFormat = Json.format[Books]
  implicit val firstScreenResponseFormat = Json.format[FirstScreenResponse]

  val saveNewBook = Form(
    mapping(
      "restautantId" -> longNumber,
      "peopleAmount" -> number,
      "day" -> nonEmptyText,
      "observation" -> optional(text)
    )(SaveNewBook.apply)(SaveNewBook.unapply)
  )

  def getDishes(restaurantId: Long) = Action.async { implicit request =>
    check { u =>
      val dishes = dishDB.findAll().map { r =>
        val grade = requestDB.findRating(r.dishId.get)
        DishGrade(r.dishId, r.name, r.description, r.imageUrl, r.price, 0, r.category, grade)
      }
      FOk(Json.toJson(dishes).toString())
    }
  }

  def getRestaurant(restaurantId: Long) = Action.async { implicit request =>
    check { u =>
      val rest = restaurantDB.findById(restaurantId).map { r =>
        val grade = bookDB.findRating(r.restaurantId.get)
        RestaurantGrade(r.restaurantId, r.name, r.address, r.imageUrl, grade)
      }.get

      FOk(Json.toJson(rest).toString())
    }
  }

  def firstScreen() = Action.async { implicit request =>
    check { u =>

      val restaurants = restaurantDB.findAll().map { r =>
        val grade = bookDB.findRating(r.restaurantId.get)
        RestaurantGrade(r.restaurantId, r.name, r.address, r.imageUrl, grade)
      }

      val books = bookDB.findByCustumer(1).flatMap { b =>
        restaurantDB.findById(b.restaurantId).map { re =>
          Books(b.bookId, re.name, re.imageUrl, true, b.people_quantity)
        }
      }

      val ret = FirstScreenResponse(restaurants, books)
      FOk(Json.toJson(ret).toString())
    }
  }

  def saveBook() = Action.async { implicit request =>
    check { u =>
      saveNewBook.bindFromRequest.fold(
        formWithErrors => {
          val error = formWithErrors.errors.toString()
          print(s"\n\nsaveBook: ${error}\n\n")
          FBadRequest(error)
        },
        pi => {
          bookDB.insert(Book(None, pi.restautantId, 1, pi.peopleAmount, pi.observation, new DateTime(), "Novo", None)) match {
            case Some(a) => FOk(Json.obj("ok" -> true).toString)
            case _       => FBadRequest(Json.obj("ok" -> false).toString)
          }
        }
      )
    }
  }
}
