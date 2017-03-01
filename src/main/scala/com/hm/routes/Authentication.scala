package com.hm.routes

import com.hm.connector.MySqlClient
import spray.http.HttpCookie
import spray.json.JsString
import spray.json._
import spray.routing.HttpService

/**
  * Created by rahul on 28/2/17.
  */
trait Authentication extends HttpService{
  def login = post{
    entity(as[String])
    {
      body=>{
        val json=body.parseJson.asJsObject
        val userName:String=if(json.getFields("userName").nonEmpty){

          json.getFields("userName").head.asInstanceOf[JsString].value
        }
        else
        {
          ""
        }
        val password = if(json.getFields("password").nonEmpty){
          json.getFields("password").head.asInstanceOf[JsString].value
        } else {
          ""
        }
        if(loginCheck(userName,password)._1){
          val uid=loginCheck(userName,password)._2
          setCookie(HttpCookie("userName",content = ""+uid)) {
            complete("User has logged in ")
          }
        }else {
          complete("wrong credentials")
        }
      }
    }

  }
  def signup =post{
    entity(as[String])
    {
      body=>{
        val json=body.parseJson.asJsObject
        val userName=json.getFields("userName").head.asInstanceOf[JsString].value
        val password=json.getFields("password").head.asInstanceOf[JsString].value
        if(!registerUser(userName,password)) {
          complete("signup successful")
        }
        else {
          complete("signup failed")
        }
      }
    }

  }
  def logout=deleteCookie("userName")
  {
    complete("user Logged out")
  }
  def loginCheck(username:String,password:String)={
    val rs = MySqlClient.getResultSet("select * from user where username='"+username+"' AND password='"+password+"'")
    val response = if(rs.next()){
      (true,rs.getInt("id"))

    }else{
      println("Invalid session")
      (false,0)
    }
    rs.close()
    response
  }
  def registerUser(userName:String,password:String)={
    val rs=MySqlClient.executeQuery("insert into user(username,password) values ('"+userName+"','"+password+"')")
    rs
  }

}
