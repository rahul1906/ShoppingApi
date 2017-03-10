package com.hm

import java.text.SimpleDateFormat
import java.util.Date

/**
  * Created by rahul on 7/3/17.
  */
object Test {

  def main(args: Array[String]): Unit = {

    val timeinmilli=1488865244029l //System.currentTimeMillis()
    val currtimemilli=1488865200000l
    val sub3mins=1488865020000l
    val sub10mins=1488864600000l

    val date=new Date()
    date.setTime(timeinmilli)
    val currDate=new Date()
    currDate.setTime(currtimemilli)
    val date1=new Date()
    date1.setTime(sub3mins)
    val date2=new Date()
    date2.setTime(sub10mins)
    val date3=new Date()
    date3.setTime(System.currentTimeMillis()-(System.currentTimeMillis()%(5*60*1000)))
    println(date3)
    println(timeinmilli-(timeinmilli%(10*60*1000)))
    println(date)
    println(currDate)
    println(date1)
    println(date2)
    println(timeinmilli)



  }

}
