package io.thoughtworks.calculator

object Calculator extends App {
  val envMap = sys.env

  // VAR1=1 VAR2=2 OPS=+ sbt run
  println("calculator 1:")
  println((new Calculator).calculate(envMap))

}

class Calculator {
  def calculate(envMap: Map[String, String]): Int = {
    val var1 = envMap("VAR1").toInt
    val var2 = envMap("VAR2").toInt
    operation(envMap)(var1)(var2)
  }

  def operation(envMap: Map[String, String]): Int => Int => Int = {
    def multiple(a: Int, b: Int): Int = a * b
    val divide: Int => Int => Int = a => b => a/b

    envMap("OPS") match {
      case "-" =>
        v1 => v2 => v1 - v2
      case "+" =>
        ((_ + _): (Int, Int) => Int).curried
      case "*" =>
        (multiple _).curried
      case "/" => divide
      case _ => throw new Error("Unsupport operation")
    }
  }
}

/**
  * 知识点：
  * 1. sealed trait
  * 2. parameter type
  * 3. case class
  * 4. case object
  * 5. Variant/Convariant
  * 6. for is map and flatMap
  * 7. previous Homeworks
  */
object Calculator2 extends App {

  object Maybe {
    def apply[A](a: A): Maybe[A] = if(a == null) No() else Yes(a)
  }

  sealed trait Maybe[A] {

    def map[B](f: A => B): Maybe[B] = this match {
      case Yes(a) => Yes(f(a))
      case No() => No()
    }
    def flatMap[B](f: A => Maybe[B]): Maybe[B] = this match {
      case Yes(a) => f(a)
      case No() => No()
    }
  }
  case class Yes[A](value: A) extends Maybe[A]
  case class No[A]() extends Maybe[A]

  val hasA = Maybe(1)
  val hasB = Yes(2)

  val c = for {
    a <- hasA
    b <- hasB
  } yield a + b

  println(c)
}

object Calculator3 extends App {
  // https://wiki.jikexueyuan.com/project/scala-development-guide/scala-class-hierarchy.html
  sealed trait Maybe[+A] {
    def map[B](f: A => B): Maybe[B] = this match {
      case Yes(a) => Yes(f(a))
      case No => No
    }
    def flatMap[B](f: A => Maybe[B]): Maybe[B] = this match {
      case Yes(a) => f(a)
      case No => No
    }
  }
  case class Yes[A](value: A) extends Maybe[A]
  case object No extends Maybe[Nothing]

  val hasA: Maybe[AnyVal] = Yes(2)
  val hasB: Maybe[AnyVal] = No
  val hasC: Maybe[AnyVal] = Yes(3)
//  for {
//    a <- hasA
//    b <- hasC
//  } yield a + b
}

object Variant extends App {
  sealed trait Human {
    def name: String
    def gendor: String
  }
  case class Man(override val name: String) extends Human {
    val gendor: String = "male"
  }
  case class Woman(override val name: String) extends Human {
    val gendor: String = "female"
  }

  val man1 = Man("1")
  val woman1 = Woman("2")

  val men1: List[Man] = List(man1)
  val men2: List[Human] = men1

  val menName: Function[Human, String] = _.name

  val menName2: Function[Man, String] = menName

}
