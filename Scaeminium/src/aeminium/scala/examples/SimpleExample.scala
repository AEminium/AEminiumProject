package aeminium.scala.examples

import aeminium.runtime.examples.Tests
import aeminium.scala._

object SimpleExample extends AeminiumApp {
  val MAX_CALC = 30
  def run = {
		  
	  val t1 = || ( task => {
	 	  println("Sum:" + 1.to(MAX_CALC).sum)
	  }) !
	  
	  ++ ( task => {
	 	  1.to(MAX_CALC/5).foreach {
	 	 	  i => println("Processing...")
	 	  }
	  }) !
	  
	  || ( task => {
	 	  println("Maximum:" + 1.to(MAX_CALC).max)
	  }) !
	  
	  || ( task => {
	 	  Tests.power(2, 20)
	  }) !
	  
	  (||(t1)( task => {
	 	  Tests.matrixMultiplication
	  }) << t1) !
  }
}
