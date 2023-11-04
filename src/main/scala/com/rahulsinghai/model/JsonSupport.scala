package com.rahulsinghai.model

import akka.http.scaladsl.marshallers.sprayjson.SprayJsonSupport
import spray.json.*

/**
 * Collects your json format instances into a support trait:
 * Author: Rahul Singhai
 */
trait JsonSupport extends SprayJsonSupport with DefaultJsonProtocol {
  import com.rahulsinghai.actor.Cart.*

  implicit val checkoutResultFormat: RootJsonFormat[CheckoutResult] = jsonFormat1(CheckoutResult.apply)
}
