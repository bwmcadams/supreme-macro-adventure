package codes.bytes.macros_intro.macros

import scala.annotation.{ compileTimeOnly, StaticAnnotation }
import scala.language.postfixOps

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros

object ADT {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    import Flag._

    // the errors should cause us to stop before this, but needed to match up our match type
    val badTree = reify {}.tree

    // check if it meets the requirements for what can be annotated
    // todo - consider a boolean flag that "Fixes" any bug...
    val inputs = annottees.map(_.tree).toList
    val result: Tree = {
      inputs match {
        case ClassDef(mods, name, tparams, impl) :: Nil ⇒
          if (mods.hasFlag(TRAIT)) {
            if (!mods.hasFlag(SEALED)) {
              c.error(c.enclosingPosition, s"ADT Root traits (trait $name) must be sealed.")
              badTree
            } else {
              c.info(c.enclosingPosition, s"ADT Root trait $name sanity checks OK.", force = true)
              ClassDef(mods, name, tparams, impl)
            }
          } else if (!mods.hasFlag(ABSTRACT)) {
            c.error(c.enclosingPosition, s"ADT Root classes (class $name) must be abstract.")
            badTree
          } else if (!mods.hasFlag(SEALED)) { // class that's abstract
            c.error(c.enclosingPosition, s"ADT Root classes (abstract class $name) must be sealed.")
            badTree
          } else {
            c.info(c.enclosingPosition, s"ADT Root type $name sanity checks OK.", force = true)
            ClassDef(mods, name, tparams, impl)
          }
        case ModuleDef(_, name, _) :: Nil ⇒
          c.error(c.enclosingPosition, s"ADT Roots (object $name) may not be Objects.")
          badTree
        // Not sure what would hit here, I checked and you cannot annotate a package object at all
        case x ⇒
          c.error(c.enclosingPosition, s"Invalid ADT Root ($x)")
          badTree
      }
    }

    c.Expr[Unit](result)
  }

}

@compileTimeOnly("Enable Macro Paradise for Expansion of Annotations via Macros.")
class ADT extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ADT.impl
}
// vim: set ts=2 sw=2 sts=2 et:
