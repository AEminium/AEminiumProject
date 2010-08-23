package aeminium.scala

import aeminium.runtime.{Body => JBody}

trait Body extends JBody {
	var result:Object = null;
	def getResult = result
}