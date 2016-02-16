package codes.bytes.macros_intro.test

import codes.bytes.macros_intro.macros.ADT
import org.scalatest.{FlatSpec, MustMatchers}

class ADTAnnotationSpec extends FlatSpec with MustMatchers {


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

  it should "Reject an unsealed abstract class" in {
    """
      | @ADT abstract class Baz
    """.stripMargin mustNot compile
  }

  it should "Reject an unsealed, non-abstract class" in {
    """
      | @ADT class NonAbstractUnsealedClass {
      |   def testBodyItem = 5
      | }
    """.stripMargin mustNot compile
  }

  it should "Reject Variables and Defs" in {
    """
      |object TestValsAndDefs {
      |  @ADT
      |  def testAnnotatingMethod = "This should blow up spectacularly."
      |
      |  @ADT
      |  val testAnnotatingVal = "This should not be allowed."
      |
      |  @ADT
      |  var testAnnotatingVar = "Mutability *and* annotative failure."
      |
      |}
    """.stripMargin
  }

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

  it should "Approve classes & traits with companions" in {
    """
      | @ADT
      | sealed abstract class TestCompanionsClass {
      |   def check = "Test Companion Class"
      | }
      | object TestCompanionsClass {
      |   def check = "Test Companion Object"
      | }
    """.stripMargin must compile

    """
      | @ADT
      | sealed trait TestCompanionsTrait {
      |   def check = "Test Companion Trait"
      | }
      | object TestCompanionsTrait {
      |   def check = "Test Companion Object"
      | }
    """.stripMargin must compile
  }

}

// vim: set ts=2 sw=2 sts=2 et:
