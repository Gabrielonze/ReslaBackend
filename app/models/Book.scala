package models

import javax.inject.{Inject, Singleton}
import anorm.SqlParser.get
import anorm._
import org.joda.time.DateTime
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

  /*def insert(perfumery: Perfumery) = DB.withConnection { implicit connection =>
    val ret = SQL("INSERT INTO book (product_id, presentation_id) values ({product_id}, {presentation_id})").
      on('product_id -> perfumery.productid, 'presentation_id -> perfumery.presentationid).
      executeInsert(SqlParser.scalar[Long].singleOpt)
    connection.close()
    ret
  }*/


}

object BookRepository {

  val simple = {
    get[Option[Long]]("book_id") ~
      get[Long]("restaurant_id") ~
      get[DateTime]("date") ~
      get[String]("status") ~
      get[String]("rating") map {
      case book_id ~ restaurant_id ~ date ~ status ~ rating =>
        Book(book_id, restaurant_id, date, status, rating)
    }
  }

}

case class Book(
                       bookId: Option[Long],
                       restaurantId: Long,
                       date: DateTime,
                       status: String,
                       rating: String
               )

object Book {
  implicit val BookFormat = Json.format[Book]
}