footer: The "WTF" of Macros - NEScala '16
slidenumbers: true
autoscale: true
build-lists: true

![original](images/major-kong-bomb.jpg)

#[fit] Scala Macros,
###[fit] or How I Learned To Stop Worrying and Mumble "WTF?!?!"


#### Brendan McAdams – <brendan@boldradius.com>
#### @rit
---

##[fit] What Are Macros?

---

![original fit](images/futurama-bender-not-reading.gif)

---

###[fit] “metaprogramming”

---

### But Seriously, What *Are* Macros?

- ‘metaprogramming’, from the Latin: ‘WTF?’.
- I mean, “code that writes code”.
- Write ‘extensions’ to Scala which expand out to more complicated code when used. Evaluated at compile time.

---

### Examples of Macros

- Facility for us to write powerful new syntax that feels ‘built-in’, such as Shapeless' “This Shouldn't Compile” `illTyped` macro...

    ```scala
    scala> illTyped { """1+1 : Int""" }
      <console>:19: error: Type-checking succeeded unexpectedly.
      Expected some error.
         illTyped { """1+1 : Int""" }
                ^
    ```

---

### Examples of Macros
- Annotations that rewrite / expand code:

    ```scala
    @hello
      object Test extends App {
        println(this.hello)
      }
    ```

- ... And a lot more.

---

### I'm Hoping To Make This Easy For You

![right](images/clockwork-eyes.gif)

- I'm pretty new to this Macro thing, and hoping to share knowledge from a beginner's standpoint.
- Without naming names, *many* Macros talks are given by Deeply Scary Sorcerers and Demigods who sometimes forget how hard this stuff is for newbies.
- Let's take a look at this through *really fresh*, profusely bleeding, eyeballs.

^ ... and learn just enough to be dangerous to ourselves (and others).

---

### Once Upon A Time...

- We could pull off a lot of what we can do with Macros, by writing compiler plugins.
- Esoteric, harder to ship (i.e. user must include a compiler plugin), not a lot of docs or examples.
- Required *deep* knowledge of the AST: Essentially generating new Scala by hand-coding ASTs.[^†]
- I've done a little bit of compiler plugin work: the AST can be tough to deal with.[^§]

^ Think of AST as nested classes that represent a tree to be converted to bytecode... or javascript!

^ Once, I helped maintain a Compiler Plugin to generate Syntax Diagrams from Parser Combinators...

[^†]: Abstract Syntax Tree. A simple “tree” of case-class like objects to be converted to bytecode... or JavaScript.

[^§]: Some of the cool stuff in Macros like Quasiquotes can be used in Compiler Plugins now, too.

---

### A Light Taste of the AST

Given a small piece of Scala code, what might the AST look like?

```scala
class StringInterp {
  val int = 42
  val dbl = Math.PI
  val str = "My hovercraft is full of eels"

  println(s"String: $str Double: $dbl Int: $int Int Expr: ${int * 1.0}")
}
```

---

### My God... It's Full of ... Uhm

```scala
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
```

---

![fit original](images/simpsons-jumpwindow.gif)

---
### Enter The Macro

- Since Scala 2.10, Macros have shipped as an experimental feature in Scala 2.10.
- Seem to have been adopted fairly quickly: I see them all over the place.
- By example, more than a few SQL Libraries have added `sql` string interpolation prefixes which generate proper JDBC Queries.
- AST Knowledge can be somewhat avoided, with some really cool tools to generate it for you.
- Much easier than compiler plugins, to add real enhanced functionality to your projects.

---

![fit original](images/futurama-hailscience.gif)


---
### Macro Paradise

- It is worth mentioning that the Macro project for Scala is evolving *quickly*.
- They release and add new features *far more frequently* than Scala does.
- “Macro Paradise” is a compiler plugin meant to bring the Macro improvements into Scala[^¶] as they become available.
- One of the features currently in Macro Paradise is Macro Annotations.
- You can learn more about Macro Paradise at [http://docs.scala-lang.org/overviews/macros/paradise.html](http://docs.scala-lang.org/overviews/macros/paradise.html)


[^¶]: focused on reliability with the current production release of Scala

---

### Macro Annotations
#### ADT Validation

- Macro Annotations are designed to let us build annotations that expand via Macros.
- The possibilites are endless, but I've written a Macro that verifies the "Root" type of an ADT is valid. The rules:
    - The root type must be either a trait or an abstract class.
    - The root type must be sealed.
- I've done this with AST manipulation to demo what that looks like.

---

### Macro Annotations
#### ADT Validation

- You can find this code at [https://github.com/bwmcadams/supreme-macro-adventure](https://github.com/bwmcadams/supreme-macro-adventure)
    - I was feeling whimsical, and used part of a suggested random repo name from Github...
- Let's look at some chunks of ScalaTest “should compile” / “should not compile” code I use to validate my ADT Macro

---

### Macro Annotations
#### ADT Validation

```scala
  "A test of annotating stuff with the ADT Compiler Annotation" should "Reject an unsealed trait" in {
    """
      | @ADT trait Foo
    """.stripMargin mustNot compile
  }

   it should "Reject a Singleton Object" in {
    """
      | @ADT object Bar
    """.stripMargin mustNot compile
  }
```

---

### Macro Annotations
#### ADT Validation

```scala
  it should "Approve a sealed trait" in {
    """
      | @ADT sealed trait Spam {
      |   def x: Int
      | }
    """.stripMargin must compile
  }

  it should "Approve a sealed, abstract class" in {
    """
      | @ADT sealed abstract class Eggs
    """.stripMargin must compile
  }

 ```

---
### ADT Validation

- So how does it all work?
- First, we need to define an annotation:

```scala
@compileTimeOnly("Enable Macro Paradise for Expansion of Annotations via Macros.")
final class ADT extends StaticAnnotation {
  def macroTransform(annottees: Any*): Any = macro ADTMacros.annotation_impl
}
```

- `@compileTimeOnly` makes sure we've enabled Macro Paradise: otherwise, our annotation fails to expand at compile time.
- `macroTransform` delegates to an actual Macro implementation which validates our ‘annottees’.

---
### ADT Validation

- A quick note on our ‘annottees’ variable...
- This annotation macro is called *once per annotated class*. The fact that it has to take varargs can be confusing.
- There is one case when we'll get more than one ‘annottee’: Companion Objects.
- If you annotate a class with a companion object, *both* are passed in.
- If you annotate an object with a companion class, only the object is passed in.

---
### The Code...

```scala
  def annotation_impl(c: whitebox.Context)(annottees: c.Expr[Any]*): c.Expr[Any] = {
    import c.universe._
    import Flag._

    val p = c.enclosingPosition

    val inputs = annottees.map(_.tree).toList

    val result: Tree = {
      // Tree manipulation code
    }

    // if no errors, return the original syntax tree
    c.Expr[Any](result)
  }

```

^ Context comes in automatically with Macros.

---
### Matching Our Tree

```scala
  inputs match {
    case (cD @ ClassDef(mods, name, tparams, impl)) :: Nil ⇒
      validateClassDef(cD, mods, name, tparams, impl, companion = None)
    // annotated class with companion object.
    case (cD @ ClassDef(mods, name, tparams, impl)) :: (mD: ModuleDef) :: Nil ⇒
      validateClassDef(cD, mods, name, tparams, impl, companion = Some(mD))
    case (o @ ModuleDef(_, name, _)) :: Nil ⇒
      c.error(p, s"ADT Roots (object $name) may not be Objects.")
      o
    // ... corner cases to follow
```

---

### Matching Our Tree

```scala
    case (d @ DefDef(mods, name, _, _, _, _)) :: Nil ⇒
      c.error(p, s"ADT Roots (def $name) may not be Methods.")
      d
    case (d @ ValDef(mods, name, _, _)) :: Nil ⇒
      if (mods.hasFlag(Flag.MUTABLE)) c.error(p, s"ADT Roots (var $name) may not be Variables.")
      else c.error(p, s"ADT Roots (val $name) may not be Variables.")
      d
    // Not sure what would hit here, I checked and you cannot annotate a package object at all
    case x :: Nil ⇒
      c.error(p, s"Invalid ADT Root ($x) [${x.getClass}].")
      x
    case Nil ⇒
      c.error(p, "Cannot validate ADT Root of empty Tree.")
      // the errors should cause us to stop before this but needed to match up our match type
      reify {}.tree
  }
```

---

![original fit](images/brilliant-pinkie-pie.png)

---

### Macros, The AST, and Def Macros

- Macros are still really built with the AST, but lately Macros provide tools to generate ASTs from code (which is what I use, mostly).
- The first, and simplest, is `reify`, which we can use to generate an AST for us.
- Let's look first at ‘Def’ Macros, which let us write Macro methods.[^‡]

[^‡]: I've stolen some code from the official Macros guide for this.

---

### A Def Macro for printf

First, we need to define a `printf` method which will ‘proxy’ our Macro definition:

```scala
// Import needed if you're *writing* a macro
import scala.language.experimental.macros
def printf(format: String, params: Any*): Unit = macro printf_impl
```

This is our macro *definition*. We also need an *implementation*.

---

### A Def Macro for printf

```scala
import scala.reflect.macros.Context
def printf_impl(c: Context)(format: c.Expr[String],
                            params: c.Expr[Any]*): c.Expr[Unit] = ???
```

We'll also want to import (in our `printf_impl` body) `c.universe._`.
This provides a lot of routine types & functions (such as `reify`).

---

### Generating The Code for `printf`

Here's our first problem: when `printf` calls `printf_impl` the Macro implementation converts all of our *values* into *syntax trees*. But we can use the AST case classes to extract:

```scala
val Literal(Constant(s_format: String)) = format.tree
```

---

### Generating The Code for `printf`

We then need code to split out the format string, and substitute parameters:

```scala
val paramsStack = Stack[Tree]((params map (_.tree)): _*)
val refs = s_format.split("(?<=%[\\w%])|(?=%[\\w%])") map {
  case "%d" => precompute(paramsStack.pop, typeOf[Int])
  case "%s" => precompute(paramsStack.pop, typeOf[String])
  case "%%" => Literal(Constant("%"))
  case part => Literal(Constant(part))
}
```

You'll note some references to `precompute`... which is another fun ball full of AST.

---

### Generating The Code for `printf`

`precompute` (a function we write ourselves) helps us convert our varargs `params` into AST statements we can reuse:

```scala
val evals = ListBuffer[ValDef]()
def precompute(value: Tree, tpe: Type): Ident = {
  val freshName = TermName(c.fresh("eval$"))
  evals += ValDef(Modifiers(), freshName, TypeTree(tpe), value)
  Ident(freshName)
}
```

In particular, we're generating a substitute name, and saving into `evals` all of the params into value definitions.

---

### Generating The Code for `printf`

Lastly, we stick it all together. Here, `reify` is used to simplify the need to generate AST objects, doing it *for* us:

```scala
val stats = evals ++ refs.map { ref =>
  reify( print(c.Expr[Any](ref).splice) ).tree
}
// our return from `printf_impl`
c.Expr[Unit](Block(stats.toList, Literal(Constant(()))))
```

Note we're using `print`, not `println`, so each individual `ref` (a.k.a block of string) is printed, using a value from `evals`. `splice` helps us graft a `reify` block onto the syntax tree.

---

### Using `printf`

It works pretty much as you'd expect:[^*]

```scala
scala> printf("%s: %d", "The Answer", 42)
The Answer: 42
```

We could, on the console, use `reify` to see how Scala expands our code:

```scala
import scala.reflect.runtime.universe._
reify(printf("%s: %d", "The Answer", 42))

res1: reflect.runtime.universe.Expr[Unit] =
  Expr[Unit](PrintfMacros.printf("%s: %d", "The Answer", 42))
```


[^*]: NOTE: You need to define your macros in a *separate* project / library from anywhere you call it.

---

### Peeking at AST Examples for “Inspiration”

Remember my first example of the AST? I actually printed it out using `reify`:

```scala
println(showRaw(reify {
  class StringInterp {
    val int = 42
    val dbl = Math.PI
    val str = "My hovercraft is full of eels"

    println(s"String: $str Double: $dbl Int: $int Int Expr: ${int * 1.0}")
  }
}.tree))
```

`.tree` will replace the `reify` ‘expansion’ code with the AST associated. `showRaw` converts it to a printable format for us.

^ I find this a great way to get examples of every which way to create Scala via ASTs.

---

![original fit](images/jonstewart-mindblown.gif)

---

### Quasiquotes for More Sanity

- There's really no way – yet – to avoid the AST Completely. But the Macro system continues to improve to give us ways to use it less and less.
- Quasiquotes, added in Scala 2.11, lets us write the equivalent of String Interpolation code that ‘evals’ to a Syntax Tree.
- We aren't going to go through a Macro build with Quasiquotes (yet), but let's look at what they do in the console...

---

### Quasiquotes in Action
#### Setting Up Our Imports

There are some implicits we need in scope for Quasiquotes Ah, the joy of imports...

```scala
import language.experimental.macros
import reflect.macros.Context
import scala.annotation.StaticAnnotation
import scala.reflect.runtime.{universe => ru}
import ru._
```

Now we're ready to generate some Syntax Trees!

---
### Quasiquotes in Action
#### Writing Some Trees

Quasiquotes look like String Interpolation, but we place a `q` in front of our string instead of `s`:

```scala
scala> q"def echo(str: String): String = str"

res4: reflect.runtime.universe.DefDef =
    def echo(str: String): String = str

```

---
### Quasiquotes in Action
#### Writing Some Trees

```scala
scala> val wtfException = q"case class OMGWTFBBQ(message: String = null) extends Exception with scala.util.control.NoStackTrace"

wtfException: reflect.runtime.universe.ClassDef =
case class OMGWTFBBQ extends Exception
    with scala.util.control.NoStackTrace
    with scala.Product
    with scala.Serializable {
  <caseaccessor> <paramaccessor> val message: String = _;
  def <init>(message: String = null) = {
    super.<init>();
    ()
}

```



---
### Extracting with Quasiquotes

It turns out, Quasiquotes can do extraction too, which I find sort of fun.

```scala
scala> val q"case class $cname(..$params) extends $parent with ..$traits { ..$body }" = wtfException
cname: reflect.runtime.universe.TypeName = OMGWTFBBQ
params: List[reflect.runtime.universe.ValDef] = List(<caseaccessor> <paramaccessor> val message: String = null)
parent: reflect.runtime.universe.Tree = Exception
traits: List[reflect.runtime.universe.Tree] = List(scala.util.control.NoStackTrace)
body: List[reflect.runtime.universe.Tree] = List()
```


^ There's a pretty cool demo of Quasiquotes to write a *Macro* yet to come...

---

![original fit](images/mrca.gif)

---


### Closing Thoughts

Macros are undoubtedly cool, and rapidly evolving. But be cautious.

> “When all you have is a hammer, everything starts to look like a thumb...”
-- me

Macros can enable great development, but also hinder it if overused. Think carefully about their introduction, and their impact on your codebase.

---

###[fit] Questions?

![original fit](images/buck-questions.jpg)
