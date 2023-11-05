package com.rahulsinghai.model

/**
 * groupSize: The amount of items that will form a group in the offer
 * itemsToBeChargedInGroup: The amount of items that will determine the cost of one group
 */
enum Offer(val groupSize: Int, val groupCost: Int):
  case NoOffer   extends Offer(1, 1)
  case Buy1Get1Free extends Offer(2, 1)
  case Buy2Get1Free  extends Offer(3, 2)

case class Item(individualCost: Double, groupSize: Int, itemsToBeChargedInGroup: Int)
