package com.rahulsinghai

import akka.actor.typed.ActorSystem
import com.rahulsinghai.server.Server

/**
 * Author: Rahul Singhai
 */
object CartApp extends App {
  implicit val system: ActorSystem[Server.Message] = ActorSystem(Server("0.0.0.0", 8080), "BuildCartServer")
}
