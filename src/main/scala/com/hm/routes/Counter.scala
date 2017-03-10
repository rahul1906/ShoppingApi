package com.hm.routes


import java.sql.Timestamp
import java.util.Date

import akka.actor.ActorSystem
import com.hm.connector.MySqlClient

import scala.collection.mutable.HashMap

/**
  * Created by rahul on 7/3/17.
  */
object Counter {
  val form = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss")
  import scala.concurrent.duration._
  import system.dispatcher
  val system=ActorSystem("on-spray-can")
  system.scheduler.schedule(5 seconds, 5 seconds) {
    println("This is a Scheduler ")
    updateCount()

    MySqlClient.getConnection.commit()
  }
  var counterMap = HashMap[Int,HashMap[Long,Int]]()
  /*
    counterMap.put("key",0)
    counterMap.get("key") match {
      case Some(count) => {}
      case None =>{}
    }

    counterMap.foreach(i=>{

    })
    */

  /* var counta=0
   var countb=0
   var countc=0*/

  def updateCount(): Unit ={
    println("indide update count")

    counterMap.foreach(i=>{
      println("inside loop  1")
      // println("KEY : "+i._1+" Count "+i._2)
      MySqlClient.statement.setInt(2,i._1)
      i._2.foreach(j=>{
        MySqlClient.statement.setInt(1,j._2)
        MySqlClient.statement1.setTimestamp(1,Timestamp.valueOf(form.format(new Date((j._1)))))
        MySqlClient.statement1.setInt(2,j._2)
        MySqlClient.statement1.setInt(3,j._2)


        i._2.put(j._1,0)
      })
    })
    MySqlClient.statement.execute()
    MySqlClient.statement1.execute()


    /*MySqlClient.statement.setInt(1,counta)
    MySqlClient.statement.setInt(2,1)
    MySqlClient.statement.execute()
    MySqlClient.statement.setInt(1,countb)
    MySqlClient.statement.setInt(2,2)
    MySqlClient.statement.execute()
    MySqlClient.statement.setInt(1,countc)
    MySqlClient.statement.setInt(2,3)

    counta=0
    countb=0
    countc=0
*/
  }

  /*def incrementCount(id:Int): Unit ={

    counterMap.get(id) match {
      case Some(count) =>counterMap.put(id,count+1)
      case None => counterMap.put(id,1)
    }

  }*/

  def incrementCount(id:Int , time:Long): Unit ={

    val t=time - (time%(10*60*1000))
    counterMap.get(id) match {
      case Some(hm) => {
        hm.get(t) match {
          case Some(count) => hm.put(t, count + 1)
          case none => hm.put(t , 1)
        }
      }
      case none => {
        val tMap = new HashMap[Long,Int]()
        tMap.put(t,1)
        counterMap.put(id,tMap)
      }
    }

  }


}
