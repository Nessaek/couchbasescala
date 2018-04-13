package com.nk.planner.repo

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
  def addAllCosts(costs: List[Int],discount: Int => Int): Int = {
    var sum = 0
    costs.foreach(sum += _)
    discount(sum)
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
}
