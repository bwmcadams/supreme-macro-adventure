package codes.bytes.macros_intro.macros

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros
import scala.annotation.{compileTimeOnly, StaticAnnotation}

object helloMacro {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._

    val result = {
      annottees.map(_.tree).toList match {
        case q"object $name extends ..$parents { ..$body }" :: Nil =>
          q"""
            object $name extends ..$parents {
              def hello: ${typeOf[String]} = "hello"
              ..$body
            }
          """
      }
    }
    c.Expr[Any](result)
  }
}

@compileTimeOnly("enable macro paradise to expand macro annotations")
class hello extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro helloMacro.impl
}

// vim: set ts=2 sw=2 sts=2 et:
