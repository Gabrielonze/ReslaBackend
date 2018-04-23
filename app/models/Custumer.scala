package models

import javax.inject.{ Inject, Singleton }
import anorm.SqlParser.get
import anorm._
import org.joda.time.DateTime
import play.api.libs.json.Json
import scala.language.postfixOps

@Singleton
class CustumerRepository @Inject() (DB: play.api.db.Database) {

  def findAll() = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM custumer").as(CustumerRepository.simple *)
    connection.close()
    ret
  }

  def findById(custumerId: Long) = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM custumer WHERE custumer_id = {custumerId}").on('custumerId -> custumerId).as(CustumerRepository.simple.singleOpt)
    connection.close()
    ret
  }

  /*def insert(perfumery: Perfumery) = DB.withConnection { implicit connection =>
    val ret = SQL("INSERT INTO custumer (product_id, presentation_id) values ({product_id}, {presentation_id})").
      on('product_id -> perfumery.productId, 'presentation_id -> perfumery.presentationId).
      executeInsert(SqlParser.scalar[Long].singleOpt)
    connection.close()
    ret
  }*/

}

object CustumerRepository {

  val simple = {
    get[Option[Long]]("custumer_id") ~
      get[String]("name") map {
        case custumer_id ~ name =>
          Custumer(custumer_id, name)
      }
  }

}

case class Custumer(
  custumerId: Option[Long],
  name: String
)

object Custumer {
  implicit val CustumerFormat = Json.format[Custumer]
}