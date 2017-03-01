package com.hm.boot

import javax.security.auth.login.Configuration

import akka.actor.{ActorSystem, Props}
import akka.io.IO
import akka.util.Timeout
import com.hm.{ServerServiceActor, config}
import spray.can.Http


/**
  * Created by rahul on 24/2/17.
  */
object Boot extends App with config.Configuration{

  implicit val system = ActorSystem("on-spray-can")
  val service = system.actorOf(Props[ServerServiceActor],"ShoppingApi")
  implicit val time = Timeout(5)
  IO(Http) ! Http.Bind(service , serviceHost , servicePort)
}
