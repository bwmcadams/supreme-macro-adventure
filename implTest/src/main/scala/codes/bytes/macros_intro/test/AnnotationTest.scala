package codes.bytes.macros_intro.test

import codes.bytes.macros_intro.macros.hello

@hello
object Test extends App {
  println(this.hello)
}