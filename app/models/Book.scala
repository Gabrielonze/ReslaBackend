package models

import javax.inject.{ Inject, Singleton }
import anorm.SqlParser.get
import anorm._
import play.api.libs.json.Json
import scala.language.postfixOps

@Singleton
class BookRepository @Inject() (DB: play.api.db.Database) {

  def findAll() = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM book").as(BookRepository.simple *)
    connection.close()
    ret
  }

  def findByid(bookid: Long) = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM book WHERE book_id = {bookid}").on('bookid -> bookid).as(BookRepository.simple.singleOpt)
    connection.close()
    ret
  }

  def findByCustumer(custumerId: Long) = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM book WHERE custumer_id = {custumerId} ORDER BY date DESC").on('custumerId -> custumerId).as(BookRepository.simple *)
    connection.close()
    ret
  }

  def findByRestaurant(restaurantId: Long) = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM book WHERE restaurant_id = {restaurantId} ORDER BY date DESC").on('restaurantId -> restaurantId).as(BookRepository.simple *)
    connection.close()
    ret
  }

  def findRating(restaurantId: Long) = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT AVG(rating) FROM book WHERE restaurant_id = {restaurantId}").on('restaurantId -> restaurantId).as(SqlParser.scalar[BigDecimal].singleOpt)
    connection.close()
    ret
  }

  def insert(b: Book) = DB.withConnection { implicit connection =>
    val ret = SQL("INSERT INTO book (restaurant_id, custumer_id, people_quantity, observation, date, status) VALUES ({restaurant_id}, {custumer_id}, {people_quantity}, {observation}, {date}, {status})").
      on('restaurant_id -> b.restaurantId, 'custumer_id -> b.custumerId, 'people_quantity -> b.people_quantity, 'observation -> b.observation, 'date -> b.date, 'status -> b.status).
      executeInsert(SqlParser.scalar[Long].singleOpt)
    connection.close()
    ret
  }

}

object BookRepository {

  val simple = {
    get[Option[Long]]("book_id") ~
      get[Long]("restaurant_id") ~
      get[Long]("custumer_id") ~
      get[Int]("people_quantity") ~
      get[Option[String]]("observation") ~
      get[String]("date") ~
      get[String]("status") ~
      get[Option[BigDecimal]]("rating") map {
        case book_id ~ restaurant_id ~ custumer_id ~ people_quantity ~ observation ~ date ~ status ~ rating =>
          Book(book_id, restaurant_id, custumer_id, people_quantity, observation, date, status, rating)
      }
  }

}

case class Book(
  bookId: Option[Long],
  restaurantId: Long,
  custumerId: Long,
  people_quantity: Int,
  observation: Option[String],
  date: String,
  status: String,
  rating: Option[BigDecimal]
)

object Book {
  implicit val BookFormat = Json.format[Book]
}