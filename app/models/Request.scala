package models

import javax.inject.{ Inject, Singleton }
import anorm.SqlParser.get
import anorm._
import org.joda.time.DateTime
import play.api.libs.json.Json
import scala.language.postfixOps

@Singleton
class RequestRepository @Inject() (DB: play.api.db.Database) {

  def findAll() = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM request").as(RequestRepository.simple *)
    connection.close()
    ret
  }

  def findByid(requestid: Long) = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM request WHERE request_id = {requestid}").on('requestid -> requestid).as(RequestRepository.simple.singleOpt)
    connection.close()
    ret
  }

  def findRating(bookId: Long) = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT AVG(rating) FROM request WHERE book_id = {bookId}").on('bookId -> bookId).as(SqlParser.scalar[BigDecimal].singleOpt)
    connection.close()
    ret
  }

  def findByBook(bookId: Long) = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM request WHERE book_id = {bookId}").on('bookId -> bookId).as(RequestRepository.simple *)
    connection.close()
    ret
  }

  /*def insert(perfumery: Perfumery) = DB.withConnection { implicit connection =>
    val ret = SQL("INSERT INTO request (product_id, presentation_id) values ({product_id}, {presentation_id})").
      on('product_id -> perfumery.productid, 'presentation_id -> perfumery.presentationid).
      executeInsert(SqlParser.scalar[Long].singleOpt)
    connection.close()
    ret
  }*/

}

object RequestRepository {

  val simple = {
    get[Option[Long]]("request_id") ~
      get[Long]("book_id") ~
      get[String]("status") ~
      get[BigDecimal]("rating") map {
        case request_id ~ book_id ~ status ~ rating =>
          Request(request_id, book_id, status, rating)
      }
  }

}

case class Request(
  requestId: Option[Long],
  bookId: Long,
  status: String,
  rating: BigDecimal
)

object Request {
  implicit val RequestFormat = Json.format[Request]
}