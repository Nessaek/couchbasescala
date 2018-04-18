package com.nk.planner.repo

import scala.concurrent.Future
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Failure, Success}

class FutureRepository {


  def addStuff(a:Int,b:Int): Future[Int] = Future {
    a + b
  }

  def minusStuff(total:Int,minus:Int):Future[Int] = Future {
    total - minus
  }

  //chained futures
  def chainedFuturesWithFor(addStuff: => Future[Int], minusStuff: => Future[Int]): Future[Int] ={
    for {
      addTotal <- addStuff
      minusTotal <- minusStuff
    }
      yield addTotal*minusTotal
  }

  //Future option
  def findKeyInList(list:List[String],key:String): Future[Option[String]] = Future{
    Thread.sleep(1000)
   list.find(name => name == key)
  }

  //Future option with Int
  def findIndexInList(list:List[String],key:String): Future[Option[Int]] = Future{
    val result = list.indexOf(key)
    println(s"$key is at index $result")
    if(result != -1) Some(result) else None
  }

  //Boolean Future
  def userHasChosen(choice:Int): Future[Boolean] = Future {
    println("2 second")
    Thread.sleep(2000)
    if(choice > 0) true else false
  }

  def printStatement(): Future[Unit] = Future{
    println("1 second")
    Thread.sleep(1000)
  }

  //List Future (for Future.sequence example)
  def futureOperations: Future[Seq[Any]] =
    Future.sequence(List(userHasChosen(1),printStatement()))

  //Chained future options(see router for implementation)
  def chainedFutureOptions(addStuff: => Future[Int], minusStuff: => Future[Int]): Future[Int] ={
    for {
      addTotal <- addStuff
      minusTotal <- minusStuff
    }
      yield addTotal*minusTotal
  }

  //Future traverse example
  def futureTraverse(futureOperationList: List[Future[Option[String]]]): Future[List[String]] = {
    Future.traverse(futureOperationList) {
      futureStuff => futureStuff.map(stuff => stuff.getOrElse("whatever"))
    }
   }

  //Future foldLeft
  def futureFoldLeft(futureOperationList: List[Future[Option[Int]]]): Future[Int] = {
    Future.foldLeft(futureOperationList)(0) { case (first, second) =>
      first + second.getOrElse(0)
    }
  }

  //Future reduceLeft
  def futureReduceLeft(futureOperationList: List[Future[Option[Int]]]): Future[Option[Int]] = {
  Future.reduceLeft(futureOperationList) { case (first, second) =>
      first.map(stuff => stuff +second.getOrElse(0))
    }
  }

  //Future firstCompletedOf
  def futureFirstCompletedOf(futureOperationList: List[Future[Option[Int]]]): Future[Option[Int]] = {
    Future.firstCompletedOf(futureOperationList)
  }

  //Future Zip
  def futureZip(list1:Future[Option[Int]],list2:Future[Option[String]]): Future[(Option[Int], Option[String])] = {
    list1 zip list2
  }


}
