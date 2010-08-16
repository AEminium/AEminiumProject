package aeminium.scala

import scala.collection.mutable.HashMap
import aeminium.runtime.{Runtime=>JRuntime, Task=>JTask, _}

trait AeminiumApp {
	val dgCache = new HashMap[Int,DataGroup]
	
	def main(args : Array[String]) : Unit = {
		Runtime.init
		run
		Runtime.shutdown
	}
	
	def run:Unit
	
	def $:DataGroup = {
		Runtime.r.createDataGroup();
	}
	
	def $(i:Int):DataGroup = {
		dgCache get i match {
			case Some(n) => n 
			case _ => {
				val dg:DataGroup = $
				dgCache += ((i,dg))
				dg
			}
		}
		
	}
	
	def || (lambda: JTask => Unit):Task = {
		new NonBlockingTask(lambda)
	}
	
	def || (parent:Task):((JTask => Unit) => Task) = {
		lambda => new NonBlockingTask(lambda, parent.ref)
	}
	
	def ++ (lambda: JTask => Unit):Task = {
		new BlockingTask(lambda)
	}
	
	def ++ (parent:Task):((JTask => Unit) => Task) = {
		lambda => new BlockingTask(lambda, parent.ref)
	}
	
	def <> (datagroup:DataGroup, lambda: JTask => Unit):Task = {
		new AtomicTask(datagroup, lambda)
	}
	
	def <> (datagroup:DataGroup, parent:Task):((JTask => Unit) => Task) = {
		lambda => new AtomicTask(datagroup, lambda, parent.ref)
	}
	
}