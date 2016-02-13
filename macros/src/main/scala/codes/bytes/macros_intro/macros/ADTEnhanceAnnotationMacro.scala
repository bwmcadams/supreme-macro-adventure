package codes.bytes.macros_intro.macros

import scala.annotation.{compileTimeOnly, StaticAnnotation}
import scala.language.postfixOps

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros

object ADT {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Unit] = {
    import c.universe._
    import Flag._

    // check if it meets the requirements for what can be annotated
    annottees.map(_.tree).toList match {
      case ClassDef(mods, name, _, _) ⇒
        println(mods)
        println(name)
        if (mods.hasFlag(TRAIT)) {
          if (!mods.hasFlag(SEALED))
            c.error(c.enclosingPosition, "ADT Root traits must be sealed.")
        } else if (!mods.hasFlag(ABSTRACT)) {
          c.error(c.enclosingPosition, "ADT Root classes must be abstract.")
        } else { // class that's abstrac
          c.error(c.enclosingPosition, "ADT Root classes must be sealed.")
        }
      case ModuleDef(_, name, _) ⇒
        c.error(c.enclosingPosition, "ADT Roots may not be Objects.")
    }

    c.Expr[Unit](reify {} tree)
  }


  def classSymbol(c: Context)(a: c.Expr[Any]): c.universe.ClassSymbol = {
    a.staticType.typeSymbol.asClass
  }
}

//@compileTimeOnly("Enable Macro Paradise for Expansion of Annotations via Macros")
class ADT extends StaticAnnotation {
  def macroCheck(annottees: Any*): Any = macro ADT.impl
}
// vim: set ts=2 sw=2 sts=2 et:
