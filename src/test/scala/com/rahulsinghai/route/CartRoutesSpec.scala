package com.rahulsinghai.route

import akka.actor as untyped
import akka.actor.testkit.typed.scaladsl.{ActorTestKit, ActorTestKitBase}
import akka.actor.typed.ActorRef
import akka.actor.{ActorSystem, Scheduler}

import akka.http.scaladsl.model.{HttpEntity, MediaTypes, StatusCodes}
import akka.http.scaladsl.server.Route
import akka.http.scaladsl.testkit.ScalatestRouteTest

import akka.util.Timeout

import com.rahulsinghai.actor.Cart
import com.rahulsinghai.actor.Cart.Command

import org.scalatest.Suite
import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

trait ScalatestTypedActorHttpRoute extends ScalatestRouteTest { this: Suite =>
  import akka.actor.typed.scaladsl.adapter.*

  implicit val typedSystem: akka.actor.typed.ActorSystem[_] = system.toTyped

  var typedTestKit:ActorTestKit = _ //val init causes createActorSystem() to cause NPE when typedTestKit.system is called in createActorSystem().
  implicit def timeout: Timeout = typedTestKit.timeout
  implicit val scheduler: untyped.Scheduler = system.scheduler

  protected override def createActorSystem(): ActorSystem = {
    typedTestKit = ActorTestKit(ActorTestKitBase.testNameFromCallStack())
    typedTestKit.system.toClassic
  }

  override def cleanUp(): Unit = {
    super.cleanUp()
    typedTestKit.shutdownTestKit()
  }
}

class CartRoutesSpec extends AnyWordSpec with Matchers with ScalatestTypedActorHttpRoute {

  val cart: ActorRef[Command] = typedTestKit.spawn(Cart(), "Cart")
  val cartRoutes: CartRoutes = new CartRoutes(cart.ref)

  "The service" should {

    "return a greeting for GET requests to the root path" in {
      Get("/") ~> cartRoutes.cartRoutes ~> check {
        responseAs[String] shouldEqual "<h1>Welcome to Scala Akka cart</h1>"
      }
    }

    "leave GET requests to other paths unhandled" in {
      Get("/invalid") ~> cartRoutes.cartRoutes ~> check {
        handled shouldBe false
      }
    }

    "return a MethodNotAllowed error for PUT requests to the root path" in {
      Put() ~> Route.seal(cartRoutes.cartRoutes) ~> check {
        status shouldEqual StatusCodes.MethodNotAllowed
        responseAs[String] shouldEqual "HTTP method not allowed, supported methods: GET, POST"
      }
    }

    "return cart total for POST requests to /checkout" in {
      Post("/v0/checkout", HttpEntity(MediaTypes.`application/json`, """[]""")) ~> cartRoutes.cartRoutes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual "£0.00"
      }

      Post("/v0/checkout", HttpEntity(MediaTypes.`application/json`, """["Apple"]""")) ~> cartRoutes.cartRoutes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual "£0.60"
      }

      Post("/v0/checkout", HttpEntity(MediaTypes.`application/json`, """["Apple", "Apple", "Orange", "Apple"]""")) ~> cartRoutes.cartRoutes ~> check {
        status shouldEqual StatusCodes.OK
        responseAs[String] shouldEqual "£2.65"
      }
    }
  }
}
