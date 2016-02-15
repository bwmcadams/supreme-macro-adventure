package codes.bytes.macros_intro.implementation

import codes.bytes.macros_intro.macros.{ADT_QQ, ADT, hello}

@hello
object Test extends App {
  println(this.hello)
}

@ADT_QQ
trait Foo

@ADT_QQ
object Bar

@ADT_QQ
abstract class Baz

@ADT_QQ
sealed trait Spam {
  def x: Int
}

final case class SpamRight(x: Int)
final case class SpamLeft(x: Int)

@ADT_QQ
sealed abstract class Eggs

@ADT_QQ
class NonAbstractUnsealedClass {
  def testBodyItem = 5
}


@ADT_QQ
sealed abstract class TestCompanionsClass {
  def check = "Test Companion Class"
}

object TestCompanionsClass {
  def check = "Test Companion Object"
}


@ADT_QQ
sealed abstract class TestCompanionsTrait {
  def check = "Test Companion Trait"
}

object TestCompanionsTrait {
  def check = "Test Companion Object"
}

package object foo {
  @ADT_QQ
  def testAnnotatingMethod = "This should blow up spectacularly."

  @ADT_QQ
  val testAnnotatingVal = "This should not be allowed."

  @ADT_QQ
  val testAnnotatingVar = "Mutability *and* annotative failure."

}
