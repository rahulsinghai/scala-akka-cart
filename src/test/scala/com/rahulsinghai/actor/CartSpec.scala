package com.rahulsinghai.actor

import akka.actor.testkit.typed.scaladsl.{BehaviorTestKit, TestInbox}

import akka.http.scaladsl.testkit.ScalatestRouteTest

import com.rahulsinghai.actor.Cart.*

import org.scalatest.matchers.should.Matchers
import org.scalatest.wordspec.AnyWordSpec

/**
 * Author: Rahul Singhai
 */
class CartSpec extends AnyWordSpec with Matchers with ScalatestRouteTest {
  // Synchronous testing
  val testKit: BehaviorTestKit[Command] = BehaviorTestKit(Cart())

  "The Cart" should {
    "return the correct total cost for a list of fruits" in {
      // For testing sending a message a TestInbox is created that provides an ActorRef and methods to assert against the messages that have been sent to it.
      val askee: TestInbox[Response] = TestInbox[Response]()
      testKit.run(Checkout(List("Apple"), askee.ref))
      askee.expectMessage(CheckoutResult("£0.60"))
    }
  }
}
