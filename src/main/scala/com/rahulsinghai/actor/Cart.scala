package com.rahulsinghai.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}

import java.text.NumberFormat
import java.util.{Currency, Locale}
import com.typesafe.config.ConfigFactory

/**
 * Author: Rahul Singhai
 */
object Cart {
  private val config = ConfigFactory.load()
  private val maxCartSize: Int = config.getInt("maxCartSize")
  private val appleCost: Double = config.getDouble("cost.apple")
  private val orangeCost: Double = config.getDouble("cost.orange")

  private val format: NumberFormat = java.text.NumberFormat.getCurrencyInstance
  private val pound: Currency = Currency.getInstance(Locale.of("en", "GB"))
  format.setCurrency(pound)

  // Trait defining successful and failure responses
  sealed trait Response
  final case class CheckoutResult(totalCost: String) extends Response
  final case class KO(reason: String) extends Response

  // Trait and its implementations representing all possible messages that can be sent to this Behavior
  sealed trait Command
  final case class Checkout(items: List[String], replyTo: ActorRef[Response]) extends Command

  // This behavior handles all possible incoming messages and keeps the state in the function parameter
  def apply(items: List[String] = List.empty[String], totalCost: Double = 0.0): Behavior[Command] = Behaviors.receive {
    case (context, Checkout(newItems, replyTo)) =>
      if(newItems.length > maxCartSize) {
        val errorMsg: String = s"Cart can not contain more than $maxCartSize items!"
        context.log.error(errorMsg)
        replyTo ! KO(errorMsg)
        Behaviors.same
      } else if((items.length + newItems.length) > maxCartSize) {
        val errorMsg: String = s"Adding these items will take count of items in the cart more than $maxCartSize items. Cart can not contain more than $maxCartSize items!"
        context.log.error(errorMsg)
        replyTo ! KO(errorMsg)
        Behaviors.same
      } else {
        context.log.info(s"Received Checkout message with items: ${newItems.mkString(", ")}")
        val newCost: Double = newItems.foldLeft(totalCost) { (accumulator, item) =>
          val itemCost = item.toLowerCase match
            case "apple" => appleCost
            case "orange" => orangeCost
            case x: String =>
              context.log.warn(s"Item $item is not yet supported!")
              0.0
          accumulator + itemCost
        }

        context.log.info(s"Items added to the cart. New cart total is: ${format.format(newCost)}")
        replyTo ! CheckoutResult(format.format(newCost))
        // Return a new behavior with updated state
        Cart(items ::: newItems, newCost)
      }
  }
}
