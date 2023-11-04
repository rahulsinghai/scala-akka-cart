package com.rahulsinghai.actor

import akka.actor.testkit.typed.scaladsl.ScalaTestWithActorTestKit

import com.rahulsinghai.actor.Cart.*

import org.scalatest.wordspec.AnyWordSpecLike

/**
 * Author: Rahul Singhai
 * ActorRefs created for asynchronous testing used as targets for asking.
 */
class CartEphemeralSpec extends ScalaTestWithActorTestKit with AnyWordSpecLike {

  override def afterAll(): Unit = testKit.shutdownTestKit()

  "An ephemeral Cart" should {
    "return the correct total cost for a list of fruits" in {
      val cart = testKit.spawn(Cart(), "Cart")
      val probe = testKit.createTestProbe[Response]()
      cart ! Checkout(List("Apple"), probe.ref)
      probe.expectMessage(CheckoutResult("£0.60"))
    }
  }
}
