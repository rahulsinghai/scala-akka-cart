package com.rahulsinghai.util

import com.rahulsinghai.model.*
import com.typesafe.config.ConfigFactory

import scala.jdk.CollectionConverters.*
import scala.util.Try

object CartConfig {
  private lazy val config = ConfigFactory.load()
  lazy val maxCartSize: Int = config.getInt("maxCartSize")

  lazy val supportedItemsMap: Map[String, Item] = config
    .getObjectList("items")
    .asScala
    .map { config =>
      val itemConfig = config.toConfig
      val name: String = itemConfig.getString("name").toLowerCase
      val individualCost: Double = itemConfig.getDouble("cost")
      val (groupSize, groupCost) = Try {
        itemConfig.getString("offer").toLowerCase match {
          case "buy1get1free" => (Offer.Buy1Get1Free.groupSize, Offer.Buy1Get1Free.groupCost)
          case "buy2get1free" => (Offer.Buy2Get1Free.groupSize, Offer.Buy2Get1Free.groupCost)
          case _ => (Offer.NoOffer.groupSize, Offer.NoOffer.groupCost) // Unknown offer
        }
      }.getOrElse((1, 1))
      name -> Item(individualCost, groupSize, groupCost)
    }
    .toMap
}
