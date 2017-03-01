package com.hm

import java.util.concurrent.Executors

import akka.actor.ActorSystem

import scala.concurrent.ExecutionContext

/**
  * Created by rahul on 24/2/17.
  */
object ServerActorSystem {

  implicit val system:ActorSystem = ActorSystem()
  implicit val ec:ExecutionContext = ExecutionContext.fromExecutor(Executors.newFixedThreadPool(2))

}
