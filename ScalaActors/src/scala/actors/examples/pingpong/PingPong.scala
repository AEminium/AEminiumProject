package scala.actors.examples.pingpong


import scala.actors._
import scala.actors.Actor._

case object Ping
case object Pong 
case object Stop

class Ping(count : Int, pong : Actor) extends Actor with AeminiumScheduler {
  def act()  {
    var pingsleft = count - 1
    pong ! Ping 
    loop {
      react {
        case Pong =>
          System.out.println("ping: received pong")
          if ( pingsleft == 0 ) {
            sender ! Stop
            exit()
          } else {
            pingsleft = pingsleft - 1
            sender ! Ping
          }
      }
    }
  }
}


class Pong extends Actor with AeminiumScheduler {
  def act() {
   loop {
      react {
        case Ping => 
          System.out.println("pong: received ping")
          sender ! Pong
        case Stop =>
          System.out.println("pong: received stop");
          exit()
      }
    }
  }
}

object PingPong extends Application 
{
  var pong = new Pong
  var ping = new Ping(10, pong)
  ping.start
  pong.start
}
