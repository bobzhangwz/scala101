package io.thoughtworks.calculator

object EitherCalculator extends App {
  val aRight = BeRight[Throwable, Int](1)
  val bRight = BeRight[Throwable, Int](2)
  val cWrong = BeWrong[Throwable, Int](new Error("wrong"))

  // for {
  //   a <- aRight
  //   b <- bRight
  //   c <- cWrong
  // } yield (a + b)

  // for {
  //   a <- aRight
  //   b <- bRight
  // } yield (a + b)
}

sealed trait Alternate[+E, +F] {
  def map[G](f: F => G): Alternate[E, G] = this match {
    case BeRight(r) => BeRight(f(r))
    case BeWrong(w) => BeWrong(w)
  }

  def flatMap[E1 >: E, G](f: F => Alternate[E1, G]) = this match {
    case BeRight(r) => f(r)
    case BeWrong(w) => BeWrong(w)
  }
}

case class BeRight[E, F](r: F) extends Alternate[E, F]
case class BeWrong[E, F](r: E) extends Alternate[E, F]
