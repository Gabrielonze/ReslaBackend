package models

import javax.inject.{ Inject, Singleton }
import anorm.SqlParser.get
import anorm._
import org.joda.time.DateTime
import play.api.libs.json.Json
import scala.language.postfixOps

@Singleton
class UserRepository @Inject() (DB: play.api.db.Database) {

  def findAll() = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM user").as(UserRepository.simple *)
    connection.close()
    ret
  }

  def findById(userId: Long) = DB.withConnection { implicit connection =>
    val ret = SQL("SELECT * FROM user WHERE user_id = {userId}").on('userId -> userId).as(UserRepository.simple.singleOpt)
    connection.close()
    ret
  }

  /*def insert(perfumery: Perfumery) = DB.withConnection { implicit connection =>
    val ret = SQL("INSERT INTO user (product_id, presentation_id) values ({product_id}, {presentation_id})").
      on('product_id -> perfumery.productId, 'presentation_id -> perfumery.presentationId).
      executeInsert(SqlParser.scalar[Long].singleOpt)
    connection.close()
    ret
  }*/

}

object UserRepository {

  val simple = {
    get[Option[Long]]("user_id") ~
      get[String]("name") map {
        case user_id ~ name =>
          User(user_id, name)
      }
  }

}

case class User(
  userId: Option[Long],
  name: String
)

object User {
  implicit val UserFormat = Json.format[User]
}