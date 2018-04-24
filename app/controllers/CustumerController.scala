package controllers

import javax.inject._

import models._
import play.api.libs.json.Json
import play.api.mvc._

@Singleton
class RestaurantController @Inject() () extends Controller with SecurityController {

  def getUser = Action.async {
    check { u =>
      FOk(Json.toJson(u).toString())
    }
  }

}
