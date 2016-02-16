package codes.bytes.macros_intro.macros

import scala.reflect.macros.blackbox

class ADTTreeImpl(c: blackbox.Context) {
  //def poly[T: c.WeakTypeTag] = c.literal(c.weakTypeOf[T].toString)

}

// vim: set ts=2 sw=2 sts=2 et:
