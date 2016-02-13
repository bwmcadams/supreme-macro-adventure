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
      def validateClassDef(
        cD: c.universe.ClassDef,
        mods: c.universe.Modifiers,
        name: c.universe.TypeName,
        tparams: List[c.universe.TypeDef],
        impl: c.universe.Template,
        companion: Option[ModuleDef]): c.universe.Tree = {

        if (mods.hasFlag(TRAIT)) {
          if (!mods.hasFlag(SEALED)) {
            c.error(c.enclosingPosition, s"ADT Root traits (trait $name) must be sealed.")
          }
          else {
            c.info(c.enclosingPosition, s"ADT Root trait $name sanity checks OK.", force = true)
          }
          companion match {
            // properly appears to append class + object in companions but uses a deprecated method
            // todo: find a good working alternate syntax.
            case Some(mD) ⇒ q"$cD; $mD"
            case None ⇒ cD
          }
        } else if (!mods.hasFlag(ABSTRACT)) {
          c.error(c.enclosingPosition, s"ADT Root classes (class $name) must be abstract.")
          badTree
        } else if (!mods.hasFlag(SEALED)) {
          // class that's abstract
          c.error(c.enclosingPosition, s"ADT Root classes (abstract class $name) must be sealed.")
          badTree
        } else {
          c.info(c.enclosingPosition, s"ADT Root class $name sanity checks OK.", force = true)
          companion match {
            // properly appears to append class + object in companions but uses a deprecated method
            // todo: find a good working alternate syntax.
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
        case ModuleDef(_, name, _) :: Nil ⇒
          c.error(c.enclosingPosition, s"ADT Roots (object $name) may not be Objects.")
          badTree
        // Not sure what would hit here, I checked and you cannot annotate a package object at all
        case x ⇒
          c.error(c.enclosingPosition, s"Invalid ADT Root ($x)")
          badTree
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
