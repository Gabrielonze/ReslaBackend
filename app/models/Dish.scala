package models

import javax.inject.{ Inject, Singleton }
import anorm.SqlParser.get
import anorm._
import org.joda.time.DateTime
import play.api.libs.json.Json
import scala.language.postfixOps

@Singleton
class DishRepository @Inject() (DB: play.api.db.Database) {

  def findAll() = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM dish").as(DishRepository.simple *)
    connection.close()
    ret
  }

  def findByid(dishid: Long) = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM dish WHERE dish_id = {dishid}").on('dishid -> dishid).as(DishRepository.simple.singleOpt)
    connection.close()
    ret
  }

  def findByRestaurant(restaurantId: Long) = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM dish WHERE restaurant_id = {restaurantId}").on('restaurantId -> restaurantId).as(DishRepository.simple *)
    connection.close()
    ret
  }

  /*def insert(perfumery: Perfumery) = DB.withConnection { implicit connection =>
    val ret = SQL("INSERT INTO dish (product_id, presentation_id) values ({product_id}, {presentation_id})").
      on('product_id -> perfumery.productid, 'presentation_id -> perfumery.presentationid).
      executeInsert(SqlParser.scalar[Long].singleOpt)
    connection.close()
    ret
  }*/

}

object DishRepository {

  val simple = {
    get[Option[Long]]("dish_id") ~
      get[Long]("restaurant_id") ~
      get[String]("name") ~
      get[String]("description") ~
      get[String]("image_url") ~
      get[BigDecimal]("price") ~
      get[String]("category") map {
        case dish_id ~ restaurant_id ~ name ~ description ~ image_url ~ price ~ category =>
          Dish(dish_id, restaurant_id, name, description, image_url, price, category)
      }
  }

}

case class Dish(
  dishId: Option[Long],
  restaurantId: Long,
  name: String,
  description: String,
  imageUrl: String,
  price: BigDecimal,
  category: String
)

object Dish {
  implicit val DishFormat = Json.format[Dish]
}