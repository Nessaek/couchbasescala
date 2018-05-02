package com.nk.couch.steps

import cucumber.api.CucumberOptions
import cucumber.api.cli.Main
import cucumber.api.junit.Cucumber
import org.junit.runner.RunWith

//@RunWith(classOf[Cucumber])
//@CucumberOptions(
//  features = Array("classpath:features"),
//  glue = Array("classpath:steps"),
//  tags = Array("@scenario"),
//  monochrome = true,
//  plugin = Array("pretty",
//    "html:target/cucumber",
//    "json:target/cucumber/test-report.json",
//    "junit:target/cucumber/test-report.xml")
//)
object TestRunner {
  def main(args: Array[String]): Unit = {

    Main.main(args)

  }
}
