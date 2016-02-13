package codes.bytes.macros_intro.implementation

import codes.bytes.macros_intro.macros.{ADT, hello}

/*
@hello
object Test extends App {
  println(this.hello)
}
*/

@ADT
abstract class TestAbstractClass
final case class TestAbstractRight(value: String)
final case class TestAbstractLeft(value: String)

@ADT
class TestClass
final case class TestClassRight(value: String)
final case class TestClassLeft(value: String)

@ADT
trait TestUnsealedTrait
final case class TestUnsealedTraitRight(value: String)
final case class TestUnsealedTraitLeft(value: String)

@ADT
sealed trait TestTrait
final case class TestTraitRight(value: String)
final case class TestTraitLeft(value: String)

