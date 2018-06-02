package com.nk.crud.model

import scala.xml.dtd.ContentModel.ElemName

case class Task(id:String,task: String, area: String, enabled:Int)

case class TaskUpdate(task: Option[String], area: Option[String], enabled:Option[Int])