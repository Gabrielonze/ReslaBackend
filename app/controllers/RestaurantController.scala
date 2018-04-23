package controllers

import javax.inject._

import models.RestaurantRepository
import play.api.libs.json.Json
import play.api.mvc._

@Singleton
class RestaurantController @Inject() (restaurantDB: RestaurantRepository) extends Controller with SecurityController {

  def getRestaurants = Action.async {
    val restaurants = restaurantDB.findAll()
    FOk(Json.toJson(restaurants).toString())
  }

}
