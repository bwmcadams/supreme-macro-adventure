package codes.bytes.macros_intro.implementation

import codes.bytes.macros_intro.macros.hello

@hello
object Test extends App {
  println(this.hello)
}
