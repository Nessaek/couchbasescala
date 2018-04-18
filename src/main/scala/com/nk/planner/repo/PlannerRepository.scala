package com.nk.planner.repo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global

class PlannerRepository {


val discount: Int = 10



  def listAllActivities(activities: List[String]):String= {
    var list: String = ""
    activities.foreach {
      list += _ + ","
    }
    return list
  }



  //Curried Function Example
  def calculateActivityCost(firstValue:Int)(secondValue:Int) = {
    println(firstValue + secondValue)
  }


  //Variable Argument Function example
  def listAllCosts(items:Int*): Unit ={
    print("the costs are" + items)
  }

  //callback example
  def addAllCosts(costs: List[Int],discount: Int => Int): Future[Int] = {
    var sum = 0
    costs.foreach(sum += _)
    Future(discount(sum))
  }


  def applyDiscount(sum:Int): Int = {
    return sum - discount
  }


    //anonymous callback example
//  def introMessage(callback: () => String): String = {
//     callback()
//  }

  //named callback example
  def introMessage(callback: => String): String = {
    callback
  }

  def callback(): String = {
    "hello everyone"
  }



  //Call by Value function Example
  def sortActivities(orders: List[(String, Int, Double)])(exchangeRate: Double): Double = {
    var totalCost: Double = 0.0
    orders.foreach {order =>
      val costOfItem = order._2 * order._3 * exchangeRate
      println(order._2)
      println(s"Cost of ${order._2} ${order._1} = £$costOfItem")
      totalCost += costOfItem
    }
    totalCost
  }

//FIXME:make own versions of these
  //Call by Name function example
  def placeOrderWithByNameParameter(orders: List[(String, Int, Double)])(exchangeRate: => Double): Double = {
    var totalCost: Double = 0.0
    orders.foreach {order =>
      val costOfItem = order._2 * order._3 * exchangeRate
      println(s"Cost of ${order._2} ${order._1} = £$costOfItem")
      totalCost += costOfItem
    }
    totalCost
  }

  //Option Example
  def findActivity(key:String, activities:List[String]):Option[String] = {
   activities.find(name => name == key)
  }

}
