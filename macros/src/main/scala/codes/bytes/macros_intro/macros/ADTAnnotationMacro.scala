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
    val result = {
      inputs match {
        case ClassDef(mods, name, tparams, impl) :: Nil ⇒
          println(mods)
          println(name)
          if (mods.hasFlag(TRAIT)) {
            if (!mods.hasFlag(SEALED))
              c.error(c.enclosingPosition, "ADT Root traits must be sealed.")
          } else if (!mods.hasFlag(ABSTRACT)) {
            c.error(c.enclosingPosition, "ADT Root classes must be abstract.")
          } else if (!mods.hasFlag(SEALED)) { // class that's abstract
            c.error(c.enclosingPosition, "ADT Root classes must be sealed.")
          }
          ClassDef(mods, name, tparams, impl)
        case ModuleDef(_, name, _) :: Nil ⇒
          c.error(c.enclosingPosition, "ADT Roots may not be Objects.")
          badTree
        case x ⇒
          println("Other: " + x)
          badTree
        /* My quasiquotes attempt, which wasn't working.
        /**
         * OK, acceptable definition for a ADT Root ...
         * by time we get into macro scala has added 'abstract' in front of trait
         */
        case q"sealed abstract trait $name extends ..$parents { ..$body }" :: Nil ⇒
          // this is a hack but having trouble figuring out how to cleanly return a Tree instead of List[Tree]
          q"sealed trait $name extends ..$parents { ..$body }"
        // OK, acceptable definition for a ADT Root
        case q"abstract class $name extends ..$parents { ..$body }" :: Nil ⇒
          // this is a hack but having trouble figuring out how to cleanly return a Tree instead of List[Tree]
          q"abstract class $name extends ..$parents { ..$body }"
        // OK, acceptable definition for a ADT Root
        case q"sealed abstract class $name extends ..$parents { ..$body }" :: Nil ⇒
          // this is a hack but having trouble figuring out how to cleanly return a Tree instead of List[Tree]
          q"sealed abstract class $name extends ..$parents { ..$body }"
        case q"class $name extends ..$parents { ..$body }" :: Nil ⇒
          c.error(c.enclosingPosition, "ADT Root classes must be abstract.")
          badTree
        case q"abstract class $name extends ..$parents { ..$body }" :: Nil ⇒
          c.error(c.enclosingPosition, "ADT Root classes must be sealed .")
          badTree
        /**
         * by time we get into macro scala has added 'abstract' in front of trait
         */
        case q"abstract trait $name extends ..$parents { ..$body }" :: Nil ⇒
          c.error(c.enclosingPosition, "ADT Root traits must be sealed.")
          badTree
        case q"object $name extends ..$parents { ..$body }" :: Nil ⇒
          c.error(c.enclosingPosition, "ADT Roots may not be Objects.")
          badTree
        case other ⇒
          c.error(c.enclosingPosition, s"$other is an unacceptable type for an ADT Root.")
          badTree*/
      }
    }

    c.Expr[Unit](result)
  }

  def classSymbol(c: Context)(a: c.Expr[Any]): c.universe.ClassSymbol = {
    a.staticType.typeSymbol.asClass
  }
}

@compileTimeOnly("Enable Macro Paradise for Expansion of Annotations via Macros")
class ADT extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ADT.impl
}
// vim: set ts=2 sw=2 sts=2 et:
