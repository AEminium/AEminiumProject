package scala.actors

import scheduler.AeminiumRuntimeScheduler

trait AeminiumScheduler extends Actor {
	protected[actors] override def scheduler: IScheduler = AeminiumRuntimeScheduler
}