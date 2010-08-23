package aeminium.scala

import scala.collection.mutable.LinkedList
import aeminium.runtime.{Runtime=>JRuntime, Task=>JTask, _}

 trait Task {
	 var parent:JTask = JRuntime.NO_PARENT;
	 var gotResult = false
	 
	 var code:JTask => Any = { task =>  null };
	 var body = new Body { 
		 	def execute(r:JRuntime, t:JTask) = { 
		 		code(t) match {
		 			case x:Unit => x
		 			case x:Object => { this.result = x.asInstanceOf[Object] }
		 		}
		 	}
		 	override def toString = "Task EM"
		}
	 def getBody:Body = body
	 
	 val ref = init
	 def init:JTask = {
		 Runtime.r.createNonBlockingTask(body, JRuntime.NO_HINTS)
	 }
	 
	  def getResult:Object = {
	 	  if (!gotResult) {
	 		  ref.getResult
	 		  gotResult=true
	 	  }
	 	  getBody.getResult
	  }
	 
	 var deps:LinkedList[JTask] = new LinkedList[JTask]
	 def << (dep:Task) = {
		 deps = deps :+ dep.ref 
		 this
	 }
	 
	 def ! = {
		 Runtime ! this
		 this
	 }
	 
 }
 
class NonBlockingTask(co:JTask => Any, par:JTask=JRuntime.NO_PARENT) extends Task {
	code = co
	parent = par
	
	override def init:JTask = {
		Runtime.r.createNonBlockingTask(body, JRuntime.NO_HINTS)
	}
}

class BlockingTask(co:JTask => Any, par:JTask=JRuntime.NO_PARENT) extends Task {
	code = co
	parent = par
	
	override def init:JTask = {
		Runtime.r.createBlockingTask(body, JRuntime.NO_HINTS)
	}
}

class AtomicTask(val datagroup:DataGroup, co:JTask => Any, par:JTask=JRuntime.NO_PARENT) extends Task {
	code = co
	parent = par
	
	override def init:JTask = {
		Runtime.r.createAtomicTask(body, datagroup, JRuntime.NO_HINTS)
	}
}