package codes.bytes.macros_intro.implementation

import codes.bytes.macros_intro.macros.{ADTMacros, ADT_QQ ⇒ ADT, hello}
import ADTMacros._

@ADT
sealed trait Spam {
  def x: Int
}

final case class SpamRight(x: Int) extends Spam
final case class SpamLeft(x: Int) extends Spam

@ADT
sealed abstract class Eggs


case object SunnySideUp extends Eggs
case object OverEasy extends Eggs
case object InABasket extends Eggs

@ADT
sealed abstract class TestCompanionsClass {
  def check = "Test Companion Class"
}

object TestCompanionsClass {
  def check = "Test Companion Object"
}


@ADT
sealed trait TestCompanionsTrait {
  def check = "Test Companion Trait"
}

object TestCompanionsTrait {
  def check = "Test Companion Object"
}

@hello
object Test extends App {
  println(this.hello)
  println("Subtypes of ADT Root Spam: " + dumpTree[Spam])
  println("Subtypes of ADT Root Eggs: " + dumpTree[Eggs])
}



