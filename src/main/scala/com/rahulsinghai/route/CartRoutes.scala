package com.rahulsinghai.route

import akka.actor.typed.{ActorRef, ActorSystem}
import akka.util.Timeout
import akka.http.scaladsl.model.{ContentTypes, HttpEntity, StatusCodes}
import akka.http.scaladsl.server.Route
import com.rahulsinghai.actor.Cart
import com.rahulsinghai.model.JsonSupport

import scala.concurrent.Future
import akka.http.scaladsl.server.Directives.*

import scala.concurrent.duration.*

/**
 * Author: Rahul Singhai
 */
class CartRoutes(cart: ActorRef[Cart.Command])(implicit system: ActorSystem[_]) extends JsonSupport {
  import akka.actor.typed.scaladsl.AskPattern.schedulerFromActorSystem
  import akka.actor.typed.scaladsl.AskPattern.Askable

  // Asking someone requires a timeout and a scheduler, if the timeout hits without response the ask is failed with a TimeoutException
  implicit val timeout: Timeout = 3.seconds

  // Define the routes for the API
  lazy val cartRoutes: Route =
    ignoreTrailingSlash {
      concat(
        get {
          pathEndOrSingleSlash {
            implicit val timeout: Timeout = 2.seconds
            complete(HttpEntity(ContentTypes.`text/html(UTF-8)`, "<h1>Welcome to Scala Akka cart</h1>"))
          }
        },
        pathPrefix("v0") {
          post {
            path("checkout") {
              // implicit val timeout: Timeout = 2.seconds
              entity(as[List[String]]) { itemsList =>
                val result: Future[Cart.Response] = cart.ask(Cart.Checkout(itemsList, _))
                onSuccess(result) {
                  case Cart.CheckoutResult(totalCost) => complete(totalCost)
                  case Cart.KO(reason) => complete(StatusCodes.InternalServerError -> reason)
                }
              }
            }
          }
        },
        pathPrefix("v1") {
          post {
            path("checkout") {
              // implicit val timeout: Timeout = 2.seconds
              entity(as[List[String]]) { itemsList =>
                val result: Future[Cart.Response] = cart.ask(Cart.Checkout(itemsList, _))
                onSuccess(result) {
                  case Cart.CheckoutResult(totalCost) => complete(totalCost)
                  case Cart.KO(reason) => complete(StatusCodes.InternalServerError -> reason)
                }
              }
            }
          }
        }
      )
    }
}
