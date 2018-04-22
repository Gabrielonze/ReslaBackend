package models

import javax.inject.{ Inject, Singleton }
import anorm.SqlParser.get
import anorm._
import org.joda.time.DateTime
import play.api.libs.json.Json
import scala.language.postfixOps

@Singleton
class OrderRepository @Inject() (DB: play.api.db.Database) {

  def findAll() = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM order").as(OrderRepository.simple *)
    connection.close()
    ret
  }

  def findByid(orderid: Long) = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM order WHERE order_id = {orderid}").on('orderid -> orderid).as(OrderRepository.simple.singleOpt)
    connection.close()
    ret
  }

  /*def insert(perfumery: Perfumery) = DB.withConnection { implicit connection =>
    val ret = SQL("INSERT INTO order (product_id, presentation_id) values ({product_id}, {presentation_id})").
      on('product_id -> perfumery.productid, 'presentation_id -> perfumery.presentationid).
      executeInsert(SqlParser.scalar[Long].singleOpt)
    connection.close()
    ret
  }*/

}

object OrderRepository {

  val simple = {
    get[Option[Long]]("order_id") ~
      get[Long]("book_id") ~
      get[String]("status") ~
      get[String]("rating") map {
        case order_id ~ book_id ~ status ~ rating =>
          Order(order_id, book_id, status, rating)
      }
  }

}

case class Order(
  orderId: Option[Long],
  bookId: Long,
  status: String,
  rating: String
)

object Order {
  implicit val OrderFormat = Json.format[Order]
}