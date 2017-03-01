package com.hm.routes

import java.lang

import com.hm.connector.MySqlClient
import spray.json.JsString
import spray.json._
import spray.routing.HttpService

/**
  * Created by rahul on 27/2/17.
  */
trait Handlers extends HttpService {



  //System.currentTimeMillis()
  //Runtime.getRuntime().totalMemory()

  def countByCustomerNo = post {
    entity(as[String]) {
      body => {


          val json = body.parseJson.asJsObject
          val customerNo = json.getFields("customerNo").head.asInstanceOf[JsString].value

          if (customerProducts(customerNo.toInt)._1) {
            complete("no of products purchased by customer is " + customerProducts(customerNo.toInt)._2 )
          }
          else {
            complete("no products purchased")
          }
       }

      }
    }
  /*parameter("productLine") { productLine =>
        if (productLineProducts(productLine)._1) {
          complete("no of products purchased by productLine is " + productLineProducts(productLine)._2)
        }
        else {
          complete("no products purchased")
        }
      }*/

    def countByProductLine = optionalCookie("userName") {
      case Some(nameCookie) => {
        val id = nameCookie.content.toInt
        post {

          entity(as[String]) {
            body => {
              val json = body.parseJson.asJsObject
              val productLine = json.getFields("productLine").head.asInstanceOf[JsString].value
              val response = productLineProducts(productLine, id.toInt)
              if (response!=0) {
                complete("no of products purchased by productLine is " + response )
              }
              else {
                complete("no products purchased")
              }
            }

          }
        }
      }
      case None => complete("No user logged in")
    }


        /*def productLineProducts(productLine: String,id:Int) = {
          val rs = MySqlClient.getResultSet("select count(productCode) as productLineCount from products where productLine='" + productLine + "'")
          MySqlClient.executeQuery("update users set cnt = (cnt+0.5) where id="+id)


          val response =
            if (rs.next()) {
              (true, rs.getInt("productLineCount"))
            }
            else {
              (false, 0)
            }
          rs.close()
          response
        }*/

  def productLineProducts(productLine: String,id:Int):Int = {
    /*val query = "select count(productCode) as productLineCount from products where productLine='" + productLine + "'"
    println(query)
    val rs = MySqlClient.getResultSet(query)*/
    val rs = MySqlClient.getResultSet("select count(productCode) as productLineCount from products where productLine='" + productLine + "'")
   //MySqlClient.executeQuery("update user set cnt = cnt+1 where id = "+id)
    MySqlClient.statement.setInt(1,id)
    MySqlClient.statement.addBatch()



    val response = if(rs.next()){
      rs.getInt("productLineCount")
    }
    else{
      0
    }
    rs.close()
    response
  }

  def customerProducts(customerNo: Int) = {
      var rs = MySqlClient.getResultSet("select count(orderNumber) as customerCount from orders where customerNumber =" + customerNo)
      val response =
        if (rs.next()) {
          (true, rs.getInt("customerCount"))
        }
        else {
          (false, 0)
        }
      rs.close()
      response
    }
  }
/* val startTime = System.currentTimeMillis()
      var totalTime:Long = 0
      (1 to 100).foreach(i=> {

        rs = MySqlClient.getResultSet("select count(productCode) as productLineCount from products where productLine='" + productLine + "'")

      })
    totalTime = System.currentTimeMillis() - startTime

     println("Average time for 100 Queries : "+totalTime/100.0)
*/