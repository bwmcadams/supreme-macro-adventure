package codes.bytes.macros_intro.test

import scala.reflect.runtime.universe._

object MacroTests extends App {

  printf("%s: %d", "The Answer", 42)

  println(showRaw(reify {
    class StringInterp {
      val int = 42
      val dbl = Math.PI
      val str = "My hovercraft is full of eels"

      println(s"String: $str Double: $dbl Int: $int Int Expr: ${int * 1.0}")
    }
  }.tree))

  /*
  Block(
    List(
      ClassDef(Modifiers(), TypeName("StringInterp"), List(), Template(
        List(Ident(TypeName("AnyRef"))), noSelfType, List(DefDef(Modifiers(), termNames.CONSTRUCTOR,
          List(),
          List(List()),
          TypeTree(), Block(List(Apply(Select(Super(This(typeNames.EMPTY), typeNames.EMPTY),
          termNames.CONSTRUCTOR), List())), Literal(Constant(())))), ValDef(Modifiers(), TermName("int"),
          TypeTree(), Literal(Constant(42))), ValDef(Modifiers(), TermName("dbl"), TypeTree(),
          Literal(Constant(3.141592653589793))), ValDef(Modifiers(), TermName("str"), TypeTree(),
          Literal(Constant("My hovercraft is full of eels"))), Apply(Select(Ident(scala.Predef),
          TermName("println")), List(Apply(Select(Apply(Select(Ident(scala.StringContext), TermName("apply")),
          List(Literal(Constant("String: ")), Literal(Constant(" Double: ")), Literal(Constant(" Int: ")),
            Literal(Constant(" Int Expr: ")), Literal(Constant("")))), TermName("s")),
          List(Select(This(TypeName("StringInterp")), TermName("str")), Select(This(TypeName("StringInterp")),
            TermName("dbl")), Select(This(TypeName("StringInterp")), TermName("int")),
            Apply(Select(Select(This(TypeName("StringInterp")), TermName("int")), TermName("$times")),
              List(Literal(Constant(1.0)))))))))
      ))), Literal(Constant(())))
      */

}
