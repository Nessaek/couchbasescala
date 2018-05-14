package com.nk.crud.model

case class Task(id:String,task: String, area: String)

case class TaskUpdate(task: Option[String], area: Option[String])