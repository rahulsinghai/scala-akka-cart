package com.rahulsinghai.actor

import akka.actor.typed.scaladsl.Behaviors
import akka.actor.typed.{ActorRef, Behavior}
import com.rahulsinghai.model.Item
import com.rahulsinghai.util.CartConfig.*

import java.text.NumberFormat
import java.util.{Currency, Locale}

/**
 * Author: Rahul Singhai
 */
object Cart {
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

  private def calculateTotalCost(itemsInCart: List[String]): (List[String], Double) = {
    // Count the occurrences of each item and store them in a Map
    val itemsInCartCountsMap: Map[String, Int] = itemsInCart.groupBy(_.toLowerCase).view.mapValues(_.size).toMap
    val totalCartCost: Double = itemsInCartCountsMap.foldLeft(0.0) { (accumulator, itemInCart) =>
      val itemInCartName = itemInCart._1
      val itemInCartCount = itemInCart._2

      val itemInCartGroupCost: Double = supportedItemsMap.get(itemInCartName) match
        case Some(item: Item) =>
          (((itemInCartCount / item.groupSize) * item.itemsToBeChargedInGroup) + (itemInCartCount % item.groupSize)) * item.individualCost
        case _ =>
          println(s"Item $itemInCartName is not yet supported!")
          0.0

      accumulator + itemInCartGroupCost
    }
    (itemsInCart.filter(x => supportedItemsMap.keySet.contains(x.toLowerCase)), totalCartCost)
  }

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
        val (newCart, newCost) = calculateTotalCost(items ::: newItems)
        context.log.info(s"Items added to the cart. New cart size is ${newCart.size} & total is: ${format.format(newCost)}")
        replyTo ! CheckoutResult(format.format(newCost))
        // Return a new behavior with updated state
        Cart(newCart, newCost)
      }
  }
}
