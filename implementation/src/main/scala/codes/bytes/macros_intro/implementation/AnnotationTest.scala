package codes.bytes.macros_intro.implementation

import codes.bytes.macros_intro.macros.{ADT, hello}

@hello
object Test extends App {
  println(this.hello)
}

@ADT
trait Bar

@ADT
object Foo
