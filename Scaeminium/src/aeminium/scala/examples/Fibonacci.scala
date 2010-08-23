package aeminium.scala.examples

import aeminium.runtime.examples.Tests
import aeminium.scala._
import aeminium.runtime.Task

object Fibonacci extends AeminiumApp {
  val MAX_CALC = 30
 
  def fib(n:Int, parent:Task):Int = { 
	  if (n < 2) 1;
	  else {
	 	  val minus1 = || ( (task => fib(n-1, task))) !
	 	  val minus2 = || ( task => fib(n-2, task)) !

	 	  minus1.getResult.asInstanceOf[Int] + minus2.getResult.asInstanceOf[Int]
 	  }
  }
  
  def run = {
	  val t1 = || { task:Task => fib(6, task) } !
	  
	  (|| (task => println("Resultado: " + t1.getResult)) << t1) !
	  
  }
}
