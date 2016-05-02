package codes.bytes.macros_intro.macros

import scala.annotation.{StaticAnnotation, compileTimeOnly}
import scala.language.experimental.macros
import scala.language.postfixOps
import scala.reflect.macros.whitebox
import scala.reflect.macros.whitebox.Context


object ADTMacros {
  def annotation_impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    import Flag._


    val p = c.enclosingPosition

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
            c.abort(p, s"ADT Root traits (trait $name) must be sealed.")
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
          c.abort(p, s"ADT Root classes (class $name) must be abstract.")
        } else if (!mods.hasFlag(SEALED)) {
          // class that's abstract
          c.abort(p, s"ADT Root classes (abstract class $name) must be sealed.")
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
          c.abort(p, s"ADT Roots (object $name) may not be Objects.")
        case (d @ DefDef(mods, name, _, _, _, _)) :: Nil ⇒
          c.abort(p, s"ADT Roots (def $name) may not be Methods.")
        case (d @ ValDef(mods, name, _, _)) :: Nil ⇒
          if (mods.hasFlag(Flag.MUTABLE))
            c.abort(p, s"ADT Roots (var $name) may not be Variables.")
          else
            c.abort(p, s"ADT Roots (val $name) may not be Variables.")
        // Not sure what would hit here, I checked and you cannot annotate a package object at all
        case x :: Nil ⇒
          c.abort(p, s"Invalid ADT Root ($x) [${x.getClass}].")
        case Nil ⇒
          c.abort(p, "Cannot validate ADT Root of empty Tree.")
      }
    }

    c.Expr[Any](result)
  }

  def dump_impl[T: c.WeakTypeTag](c: whitebox.Context): c.Expr[Set[String]] = {

    import c.universe._

    val p = c.enclosingPosition

    val typ = c.weakTypeOf[T].typeSymbol.asClass

    val subs = typ.knownDirectSubclasses.map(_.fullName)

    if (subs.isEmpty)
      c.warning(p, s"Cannot locate subclasses for ${typ.fullName}; this may be related to SI-7046. Try invoking `dump_tree` after the ADT compilation site")

    c.Expr[Set[String]](q"$subs")
  }

  def dumpTree[T]: Set[String] = macro dump_impl[T]
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
  def macroTransform(annottees: Any*): Any = macro ADTMacros.annotation_impl
}
// vim: set ts=2 sw=2 sts=2 et:
