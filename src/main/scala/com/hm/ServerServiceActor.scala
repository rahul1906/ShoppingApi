package com.hm

import akka.actor.Actor
import com.hm.routes.Routes

import scala.concurrent.ExecutionContext


/**
  * Created by rahul on 24/2/17.
  */
class ServerServiceActor extends Actor with Routes{

  //Akka Actor Context is set as the actorRefFactory
  def actorRefFactory = context
  //rootRoute is defined in "com.hm.routes.Routes"
  def receive = runRoute(route)
  implicit def dispatcher: ExecutionContext = ServerActorSystem.ec

}
