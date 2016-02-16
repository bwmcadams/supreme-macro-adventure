package codes.bytes.macros_intro.implementation

import codes.bytes.macros_intro.macros.{ADT, hello}

@hello
object Test extends App {
  println(this.hello)
}



@ADT
sealed trait Spam {
  def x: Int
}

final case class SpamRight(x: Int)
final case class SpamLeft(x: Int)

@ADT
sealed abstract class Eggs

@ADT
sealed abstract class TestCompanionsClass {
  def check = "Test Companion Class"
}

object TestCompanionsClass {
  def check = "Test Companion Object"
}


@ADT
sealed abstract class TestCompanionsTrait {
  def check = "Test Companion Trait"
}

object TestCompanionsTrait {
  def check = "Test Companion Object"
}

