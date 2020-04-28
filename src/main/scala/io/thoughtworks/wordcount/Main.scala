package io.thoughtworks.wordcount

import scala.io.Source
import scala.util.Try

object Main {

  def main(args: Array[String]): Unit = {
    print("hello world")
  }
}

object Main2 extends App {
  Try {

  }.toEither
  println(
    Source.fromURL(this.getClass.getClassLoader.getResource("ne.txt")).getLines.toList.mkString
  )
}
