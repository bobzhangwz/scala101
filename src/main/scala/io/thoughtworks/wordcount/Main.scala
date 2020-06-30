package io.thoughtworks.wordcount

import scala.io.Source
import scala.util.Try

object Main {

  def main(args: Array[String]): Unit = {
    print("hello world")
  }
}

object Main2 extends App {
  val wordCount = new WordCount
  val input = Try {
    Source.fromURL(this.getClass.getClassLoader.getResource("news.txt")).getLines.toList.mkString
  }.toEither

  input.map(wordCount.listWords).fold(
    error => println(error),
    _.reverse.take(10).foreach { case WordFrequency(word, counts) => println(s"$word $counts") }
  )
}

object Main3 extends App {
  // INPUT=URL@https://github.com/bobzhangwz/scala101/raw/workshop03/src/test/scala/resources/helloworld.txt sbt run
  val inputSource = EnvParser.parse(sys.env.get("INPUT").get)
  println(EnvParser.getContentFromSource(inputSource))
}
