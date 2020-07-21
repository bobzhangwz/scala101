package io.thoughtworks.calculator

object HelloOption extends App {
  val aOption: Option[Int] = Some(1)
  val bOption: Option[Int] = Some(2)
  val c =
    for {
      a <- aOption
      b <- bOption
    } yield a + b
  //  aOption.flatMap(a => bOption.map(b => a + b))
  println(c)
}

object HelloOption2 extends App {
  val aOption: Option[Int] = Some(1)
  val bOption: Option[Int] = Some(2)
  val cOption: Option[Int] = None
  val c =
    for {
      a <- aOption
      b <- bOption
      _ <- cOption
    } yield a + b

  println(c)
}

object HelloOption3 extends App {
  val aOption: Option[Int] = Some(1)
  val bOption: Option[Int] = Some(2)
  val cOption: Option[Int] = None
  val c =
    for {
      a <- aOption
      _ <- cOption
      b <- bOption
    } yield a + b

  println(c)
}
