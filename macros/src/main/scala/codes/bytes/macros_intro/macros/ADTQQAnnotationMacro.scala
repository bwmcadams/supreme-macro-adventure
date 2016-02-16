package codes.bytes.macros_intro.macros

import scala.annotation.{ compileTimeOnly, StaticAnnotation }
import scala.language.postfixOps

import scala.reflect.macros.blackbox.Context
import scala.language.experimental.macros

object ADT_QQ {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    import Flag._

    val inputs = annottees.map(_.tree).toList

    val result: Tree = {
      // check if it meets the requirements for what can be annotated
      inputs match {
        case (t @ q"$mods trait $name") :: Nil ⇒
          if (!mods.hasFlag(SEALED)) {
            c.error(c.enclosingPosition, s"ADT Root traits (trait $name) must be sealed.")
          } else {
            c.info(c.enclosingPosition, s"ADT Root trait $name sanity checks OK.", force = true)
          }
          t
        case (cls @ q"$mods class $name") :: Nil ⇒
          if (!mods.hasFlag(ABSTRACT)) {
            c.error(c.enclosingPosition, s"ADT Root classes (class $name) must be abstract.")
          } else if (!mods.hasFlag(SEALED)) {
            // class that's abstract
            c.error(c.enclosingPosition, s"ADT Root classes (abstract class $name) must be sealed.")
          } else {
            c.info(c.enclosingPosition, s"ADT Root class $name sanity checks OK.", force = true)
          }
          cls
        /**
         * According to the docs, if you annotate a *class* with a companion,
         * the class and companion will be sent in (e.g., List(class, object) for
         * entry Tree. If you annotate an *object* with a companion, only the object
         * is passed in.
         *
         *  Using ClassDef match, Scala will refuse to accept returned Tree unless it
         *  includes both companions sent in. For QuasiQuotes it seems to ignore that,
         *  yet the object still works fine after Macro transform.
         */
        case (o @ q"$mods object $name") :: Nil ⇒
          c.error(c.enclosingPosition, s"ADT Roots (object $name) may not be Objects.")
          o
        // TODO: We need to check for defs and vals, which would hit this
        // I checked and you cannot annotate a package object at all
        case x :: Nil ⇒
          c.error(c.enclosingPosition, s"Invalid ADT Root ($x)")
          x
        case Nil ⇒
          c.error(c.enclosingPosition, s"Cannot ADT Validate an empty Tree.")
          reify {} .tree
      }
    }

    c.Expr[Any](result)
  }

}

/**
 * From the Macro Paradise Docs...
 *
 * note the @compileTimeOnly annotation. It is not mandatory, but is recommended to avoid confusion.
 * Macro annotations look like normal annotations to the vanilla Scala compiler, so if you forget
 * to enable the macro paradise plugin in your build, your annotations will silently fail to expand.
 * The @compileTimeOnly annotation makes sure that no reference to the underlying definition is
 * present in the program code after typer, so it will prevent the aforementioned situation
 * from happening.
 */
@compileTimeOnly("Enable Macro Paradise for Expansion of Annotations via Macros.")
final class ADT_QQ extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ADT.impl
}
// vim: set ts=2 sw=2 sts=2 et:
