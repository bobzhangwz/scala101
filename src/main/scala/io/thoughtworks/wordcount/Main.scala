package io.thoughtworks.wordcount

import scala.io.Source

object Main {

  def main(args: Array[String]): Unit = {
    print("hello world")
  }
}

object Main2 extends App {
  println(
    Source.fromURL(this.getClass.getClassLoader.getResource("news.txt")).getLines.toList.mkString
  )
}
