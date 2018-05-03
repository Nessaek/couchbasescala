package com.nk.couch.model

case class Activity(activity: String, area: String)

case class ActivityWithId(id:String) extends Activity