package io.thoughtworks.calculator

import scala.util.Try

object Calculator extends App {
  val envMap = sys.env

  // VAR1=1 VAR2=2 OPS=+ sbt run
  println("calculator 1:")
  println((new Calculator).calculate(envMap))

  println("calculator 2:")
  println((new Calculator2).calculate(envMap))
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

class Calculator2 {
  def calculate(envMap: Map[String, String]): Int = {
    val var1 = envMap("VAR1").toInt
    val var2 = envMap("VAR2").toInt
    operation(envMap)(var1, var2)
  }

  def operation(envMap: Map[String, String]): (Int, Int) => Int = ??? 
}

class Calculator3 {
  def calculate(envMap: Map[String, String]): Option[Int] = {
    val var1: Option[Int] = envMap.get("VAR1").map(_.toInt)
    val var2: Option[Int] = envMap.get("VAR2").map(_.toInt)
    var1.flatMap(v1 =>
      var2.flatMap(v2 =>
        operation(envMap).map(
          ops => ops(v1)(v2)
        )
      )
    )
  }

  def operation(envMap: Map[String, String]): Option[Int => Int => Int] =
    envMap.get("OPS").map { _ match {
      case "-" =>
        v1 => v2 => v1 - v2
      case "+" =>
        v1 => v2 => v1 + v2
      case "*" =>
        v1 => v2 => v1 * v2
      case "/" => v1 => v2 => v1 / v2
      case _ => throw new Error("Unsupport operation")
    }}
}

class Calculator4 {
  def calculate(envMap: Map[String, String]): Option[Int] = {
    val var1: Option[Int] = envMap.get("VAR1").map(_.toInt)
    val var2: Option[Int] = envMap.get("VAR2").map(_.toInt)
    for {
      v1 <- var1
      v2 <- var2
      ops <- operation(envMap)
    } yield ops(v1)(v2)
  }

  def operation(envMap: Map[String, String]): Option[Int => Int => Int] = envMap.get("OPS").map {
    case "-" =>
      v1 => v2 => v1 - v2
    case "+" =>
      v1 => v2 => v1 + v2
    case "*" =>
      v1 => v2 => v1 * v2
    case "/" =>
      v1 => v2 => v1 / v2
    case _ => throw new Error("Unsupport operation")
  }
}

class Calculator5 {
  def calculate(envMap: Map[String, String]): Either[Throwable, Int] = for {
    var1 <- envMap.get("VAR1").toRight(new Error("VAR1 not exist"))
    var2 <- envMap.get("VAR2").toRight(new Error("VAR2 not exist"))

    v1 <- var1.toIntOption.toRight(new Error("can not parse value"))
    v2 <- Try { var2.toInt }.toEither
    ops <- operation(envMap)
  } yield ops(v1)(v2)

  def operation(envMap: Map[String, String]): Either[Throwable, Int => Int => Int] = {
    envMap.get("OPS").toRight(new Error("OPS not exists")).flatMap {
      case "-" => Right(v1 => v2 => v1 - v2)
      case "+" => Right(v1 => v2 => v1 + v2)
      case "*" => Right(v1 => v2 => v1 * v2)
      case "/" => Right(v1 => v2 => v1 / v2)
      case ops => Left(new Error(s"Unsupported operator $ops"))
    }
  }
}
