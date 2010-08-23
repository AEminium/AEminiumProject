package aeminium.scala

import scala.collection.mutable.LinkedList
import aeminium.runtime.implementations.Factory
import aeminium.runtime.{Runtime=>JRuntime, Task=>JTask, _}

object Runtime {
	
	val r:JRuntime = Factory.getRuntime
	
	def ! (task:Task) {
		r.schedule (task.ref, task.parent, java.util.Arrays.asList (task.deps.toArray: _*))
	}
	
	def init = r.init
	def shutdown = {
		r.shutdown
	}
}