package aeminium.scala

import scala.collection.mutable.LinkedList
import aeminium.runtime.{Runtime=>JRuntime, Task=>JTask, _}

 trait Task {
	 val parent:JTask = null;
	 
	 val code = { t:JTask => () }
	 var body = new Body { def execute(r:JRuntime, t:JTask) = code(t) }
	 def getBody:Body = body
	 
	 val ref = init
	 def init:JTask = {
		 Runtime.r.createNonBlockingTask(body, JRuntime.NO_HINTS)
	 }
	 
	 var deps:LinkedList[JTask] = new LinkedList[JTask]
	 def << (dep:Task) = {
		 deps :+ dep.ref 
		 this
	 }
	 
	 def ! = {
		 Runtime ! this
		 this
	 }
	 
	 var result:Any = null
	 def setResult(r:Any) = { result = r }
	 def getResult:Object = result.asInstanceOf[Object]
	 
 }
 
class NonBlockingTask(override val code:JTask => Unit, override val parent:JTask=JRuntime.NO_PARENT) extends Task {
	override def init:JTask = {
		Runtime.r.createNonBlockingTask(body, JRuntime.NO_HINTS)
	}
}

class BlockingTask(override val code:JTask => Unit, override val parent:JTask=JRuntime.NO_PARENT) extends Task {
	override def init:JTask = {
		Runtime.r.createBlockingTask(body, JRuntime.NO_HINTS)
	}
}

class AtomicTask(val datagroup:DataGroup, override val code:JTask => Unit, override val parent:JTask=JRuntime.NO_PARENT) extends Task {
	override def init:JTask = {
		Runtime.r.createAtomicTask(body, datagroup, JRuntime.NO_HINTS)
	}
}