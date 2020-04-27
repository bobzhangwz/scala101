package io.thoughtworks.wordcount

case class WordFrequency(word: String, counts: Int)

class WordCount {
  def listWords(text: String): List[WordFrequency]      = ???
  def countWords(words: List[String]): Map[String, Int] = ???
  def extractWords(text: String): List[String]          = ???
}
