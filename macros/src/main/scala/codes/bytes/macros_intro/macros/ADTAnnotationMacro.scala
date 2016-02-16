package codes.bytes.macros_intro.macros

import scala.annotation.{ compileTimeOnly, StaticAnnotation }
import scala.language.postfixOps

import scala.reflect.macros.whitebox.Context
import scala.language.experimental.macros

object ADT {
  def impl(c: Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    import Flag._


    val p = c.enclosingPosition.withPoint(c.enclosingPosition.point + 1)

    // check if it meets the requirements for what can be annotated
    val inputs = annottees.map(_.tree).toList
    val result: Tree = {
      def validateClassDef(
        cD: c.universe.ClassDef,
        mods: c.universe.Modifiers,
        name: c.universe.TypeName,
        tparams: List[c.universe.TypeDef],
        impl: c.universe.Template,
        companion: Option[ModuleDef]): c.universe.Tree = {

        if (mods.hasFlag(TRAIT)) {
          if (!mods.hasFlag(SEALED)) {
            c.error(p, s"ADT Root traits (trait $name) must be sealed.")
          }
          else {
            c.info(p, s"ADT Root trait $name sanity checks OK.", force = true)
          }
          companion match {
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
            case Some(mD) ⇒ q"$cD; $mD"
            case None ⇒ cD
          }
        } else if (!mods.hasFlag(ABSTRACT)) {
          c.error(p, s"ADT Root classes (class $name) must be abstract.")
          cD
        } else if (!mods.hasFlag(SEALED)) {
          // class that's abstract
          c.error(p, s"ADT Root classes (abstract class $name) must be sealed.")
          cD
        } else {
          c.info(p, s"ADT Root class $name sanity checks OK.", force = true)
          companion match {
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
            case Some(mD) ⇒ q"$cD; $mD"
            case None ⇒ cD
          }
        }
      }

      inputs match {
        case (cD @ ClassDef(mods, name, tparams, impl)) :: Nil ⇒
          validateClassDef(cD, mods, name, tparams, impl, companion = None)
        // annotated class with companion object.
        // In the case of an annotated class/object w/ a companion, the companion is passed to
        // annottees. We *are* assuming the class is one annotated here.
        case (cD @ ClassDef(mods, name, tparams, impl)) :: (mD: ModuleDef) :: Nil ⇒
          validateClassDef(cD, mods, name, tparams, impl, companion = Some(mD))
        case (o @ ModuleDef(_, name, _)) :: Nil ⇒
          c.error(p, s"ADT Roots (object $name) may not be Objects.")
          o
        case (d @ DefDef(mods, name, _, _, _, _)) :: Nil ⇒
          c.error(p, s"ADT Roots (def $name) may not be Methods.")
          d
        case (d @ ValDef(mods, name, _, _)) :: Nil ⇒
          if (mods.hasFlag(Flag.MUTABLE))
            c.error(p, s"ADT Roots (var $name) may not be Variables.")
          else
            c.error(p, s"ADT Roots (val $name) may not be Variables.")
          d
        // Not sure what would hit here, I checked and you cannot annotate a package object at all
        case x :: Nil ⇒
          c.error(p, s"Invalid ADT Root ($x) [${x.getClass}].")
          x
        case Nil ⇒
          c.error(p, "Cannot validate ADT Root of empty Tree.")
          // the errors should cause us to stop before this,
          // but needed to match up our match type
          reify {}.tree
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
final class ADT extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ADT.impl
}
// vim: set ts=2 sw=2 sts=2 et:
