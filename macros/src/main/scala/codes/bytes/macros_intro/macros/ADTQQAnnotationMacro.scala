package codes.bytes.macros_intro.macros

import scala.annotation.{ compileTimeOnly, StaticAnnotation }
import scala.language.postfixOps
import scala.reflect.macros.whitebox

import scala.language.experimental.macros

object ADT_QQ {
  def impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    import Flag._

    val p = c.enclosingPosition

    val inputs = annottees.map(_.tree).toList

    val result: Tree = inputs match {
      case (t @ q"$flags trait $name extends ..$parents { ..$body }") :: Nil if flags.hasFlag(SEALED) ⇒
        c.info(p, s"ADT Root trait $name sanity checks OK.", force = true)
        t
      case (t @ q"$flags trait $name extends ..$parents { ..$body }") :: Nil ⇒
        c.error(p, s"ADT Root traits (trait $name) must be sealed.")
        t
      case (cls @ q"$flags class $name extends ..$parents { ..$body }") :: Nil if flags.hasFlag(ABSTRACT) && flags.hasFlag(SEALED) ⇒ // there's no bitwise AND (just OR) on Flags
        c.info(p, s"ADT Root class $name sanity checks OK.", force = true)
        cls
      case (cls @ q"$flags class $name extends ..$parents { ..$body }") :: Nil ⇒
        c.error(p, s"ADT Root classes (class $name) must be abstract and sealed.")
        cls
      case (o @ q"$flags object $name") :: Nil ⇒
        c.error(p, s"ADT Roots (object $name) may not be Objects.")
        o
      // companions
      case (t @ q"$flags trait $name extends ..$parents { ..$body }") :: (mD: ModuleDef):: Nil if flags.hasFlag(SEALED) ⇒
        c.info(p, s"ADT Root trait $name sanity checks OK.", force = true)
        q"$t; $mD"
      case (t @ q"$flags trait $name extends ..$parents { ..$body }") :: (mD: ModuleDef) :: Nil ⇒
        c.error(p, s"ADT Root traits (trait $name) must be sealed.")
        q"$t; $mD"
      case (cls @ q"$flags class $name extends ..$parents { ..$body }") :: (mD: ModuleDef) :: Nil⇒ // there's no bitwise AND (just OR) on Flags
        c.info(p, s"ADT Root class $name sanity checks OK.", force = true)
        q"$cls; $mD"
      case (cls @ q"$flags class $name extends ..$parents { ..$body }") :: (mD: ModuleDef) :: Nil ⇒
        c.error(p, s"ADT Root classes (class $name) must be abstract and sealed.")
        q"$cls; $mD"
      // method definition
      case (d @ q"def $name = $body") :: Nil ⇒
        c.error(p, s"ADT Roots (def $name) may not be Methods.")
        d
      // immutable variable definition
      case (v @ q"val $name = $value") :: Nil ⇒
        c.error(p, s"ADT Roots (val $name) may not be Variables.")
        v
      case (v @ q"var $name = $value") :: Nil ⇒
        c.error(p, s"ADT Roots (var $name) may not be Variables.")
        v
      // I checked and you cannot annotate a package object at all
      case x :: Nil ⇒
        c.error(p, s"! Invalid ADT Root ($x) ${x.getClass}")
        x
      case Nil ⇒
        c.error(p, s"Cannot ADT Validate an empty Tree.")
        reify {} .tree
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
  def macroTransform(annottees: Any*): Any = macro ADT_QQ.impl
}
// vim: set ts=2 sw=2 sts=2 et:
