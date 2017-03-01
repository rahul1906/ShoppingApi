package com.hm.connector

import java.sql.{Connection, DriverManager, PreparedStatement, ResultSet}
import java.util.concurrent.{ScheduledThreadPoolExecutor, TimeUnit}
import scala.collection.mutable.ArrayBuffer
import akka.actor.ActorSystem
import scala.language.postfixOps
/**
  * Created by rahul on 27/2/17.
  */
object MySqlClient {


  private val dbc = "jdbc:mysql://" + "127.0.0.1" + ":" + 3306 + "/" + "classicmodels" + "?user=" + "root" + "&password=" + "root"
  classOf[com.mysql.jdbc.Driver]
 // private var conn: Connection = DriverManager.getConnection(dbc)
 private var conn: Connection = null

  def getConnection: Connection = {
    if(conn ==null) {
      conn = DriverManager.getConnection(dbc)
      conn.setAutoCommit(false)
      conn
    }else if (conn.isClosed) {
      conn = DriverManager.getConnection(dbc)
      conn.setAutoCommit(false)
      conn
    }else{
      conn
    }

  }


  /* def getConnection: Connection = {
    if (conn.isClosed) {
      conn = DriverManager.getConnection(dbc)
    }
    conn
  }*/
  val autoIncValuesForTable: Map[String, Array[String]] = Map(
    "grp" -> Array("id")

  )

  val statement=MySqlClient.getConnection.prepareStatement("update user set cnt = cnt+1 where id = ?")

  def closeConnection() = conn.close()

  def executeQuery(query: String): Boolean = {
    val statement = getConnection.createStatement()
    try
      statement.execute(query)
    finally statement.close()
  }

  def getResultSet(query: String): ResultSet={
    val statement=getConnection.createStatement()
    statement.executeQuery(query)
  }

  def insert(tableName: String, elements: Map[String, Any]): Int = {
    try {
      val colNames: ArrayBuffer[String] = ArrayBuffer()
      val values: ArrayBuffer[Any] = ArrayBuffer()
      elements.foreach(i => {
        colNames += i._1
        values += i._2
      })

      val insertQuery = "INSERT INTO " + tableName + " (" + colNames.mkString(",") + ") VALUES (" + colNames.indices.map(i => "?").mkString(",") + ")"

      val returnColumns: Array[String] = autoIncValuesForTable.getOrElse(tableName, Array())
      val preparedStatement: PreparedStatement = getConnection.prepareStatement(insertQuery, returnColumns)

      values.zipWithIndex.foreach(i => addToPreparedStatement(i._1, i._2 + 1, preparedStatement))
      var generatedId: Int = 0
      try {

        preparedStatement.executeUpdate()
        if (returnColumns.nonEmpty) {
          val gkSet = preparedStatement.getGeneratedKeys
          if (gkSet.next()) {
            generatedId = gkSet.getInt(1)
          }
        }
      }
      finally preparedStatement.close()

      generatedId
    } catch {
      case e: Exception => e.printStackTrace()
        0
    }
  }
  private def addToPreparedStatement(value: Any, index: Int, preparedStatement: PreparedStatement) = {
    value match {
      case v: Long => preparedStatement.setLong(index, v)
      case v: Int => preparedStatement.setInt(index, v)
      case v: Double => preparedStatement.setDouble(index, v)
      case v: String => preparedStatement.setString(index, v)

      case v: Array[Byte] => preparedStatement.setBytes(index, v)
      case v: Serializable => preparedStatement.setObject(index, v)
      case _ => preparedStatement.setString(index, value.toString)
    }
  }

  import system.dispatcher
  import scala.concurrent.duration._
  // ...now with system in current scope:
  val system=ActorSystem("on-spray-can")
  system.scheduler.schedule(1 seconds, 1 seconds) {
    MySqlClient.statement.executeBatch()
    MySqlClient.getConnection.commit()
  }
}



