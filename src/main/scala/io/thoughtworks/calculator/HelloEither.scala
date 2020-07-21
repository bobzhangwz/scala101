package io.thoughtworks.calculator

object HelloEither extends App {
  val aRight: Either[Throwable, Int] = Right(1)
  val bRight: Either[Throwable, Int] = Right(2)
  val cLeft: Either[Throwable, Int] = Left(new Error("bad parameters"))

  val result = for {
    a <- aRight
    b <- bRight
  } yield a + b

  println(result)

  val result2 = for {
    a <- aRight
    b <- bRight
    _ <- cLeft
  } yield a + b

  println(result2)

}

// 作业： 参考Either的功能, 编写SuccessOrFail
