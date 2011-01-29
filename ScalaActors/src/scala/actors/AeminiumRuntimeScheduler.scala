package scala.actors
package scheduler

import scala.actors.SchedulerAdapter
import scala.actors.Debug

import aeminium.runtime.Runtime
import aeminium.runtime.Body
import aeminium.runtime.Task
import aeminium.runtime.implementations.Factory

object AeminiumRuntimeScheduler extends Runnable with IScheduler with TerminationMonitor {
	protected val rt : Runtime = Factory.getRuntime();
	protected val CHECK_FREQ   = 10
	protected var isShutdown   = false;
	
	def execute(task: Runnable): Unit =  execute { task.run() }
	
	override def execute(fun: => Unit) : Unit = {
		Debug.info("AeminiumScheduler: execute")
		val body = new Body {
			def execute(rt : Runtime, t : Task) {
				fun
			}
		}
		val task = rt.createNonBlockingTask(body, Runtime.NO_HINTS);
		Debug.info("AeminiumScheduler task = "+task)
		rt.schedule(task, Runtime.NO_PARENT, Runtime.NO_DEPS);
	}
	
	def start() {
		Debug.info("AeminiumScheduler: start background thread");
	    val t = new Thread(this)
	    t.setDaemon(true)
        t.setName("AeminiumScheduler")
        t.start()
        
        Debug.info("AeminiumScheduler: initialize runtime");
        rt.init();
	}
	
	override def run () {
		Debug.info("AeminiunScheduler run with rt = " + rt);

		this.synchronized {
			while ( !(isShutdown || allActorsTerminated) ) {
				wait(CHECK_FREQ)
			}
		}
		
		Debug.info("AeminiumScheduler: finalize");
		rt.shutdown();
	}
	
	override def shutdown() {
		this.synchronized {
			isShutdown = true;
		}
	}

	def isActive: Boolean = Scheduler.isActive

    def managedBlock(blocker: scala.concurrent.ManagedBlocker) {
		blocker.block()
	}
	
	// start the background thread
	start()
}