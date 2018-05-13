package models

import javax.inject.{ Inject, Singleton }
import anorm.SqlParser.get
import anorm._
import org.joda.time.DateTime
import play.api.libs.json.Json
import scala.language.postfixOps

@Singleton
class RestaurantRepository @Inject() (DB: play.api.db.Database) {

  def findAll() = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM restaurant").as(RestaurantRepository.simple *)
    connection.close()
    ret
  }

  def findById(restaurantId: Long) = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM restaurant WHERE restaurant_id = {restaurantId}").on('restaurantId -> restaurantId).as(RestaurantRepository.simple.singleOpt)
    connection.close()
    ret
  }

}

object RestaurantRepository {

  val simple = {
    get[Option[Long]]("restaurant_id") ~
      get[String]("name") ~
      get[String]("image_url") ~
      get[String]("endereco") map {
        case restaurant_id ~ name ~ image_url ~ endereco =>
          Restaurant(restaurant_id, name, image_url, endereco)
      }
  }

}

case class Restaurant(
  restaurantId: Option[Long],
  name: String,
  imageUrl: String,
  address: String
)

object Restaurant {
  implicit val RestaurantFormat = Json.format[Restaurant]
}