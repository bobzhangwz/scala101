package io.thoughtworks.wordcount

case class WordFrequency(word: String, counts: Int)

class WordCount {
  def listWords(text: String): List[WordFrequency] =
    (extractWords _).andThen(countWords).apply(text).map {
      case (word, count) => WordFrequency(word, count)
    }.toList.sortBy(_.counts)

  private[wordcount] def countWords(words: List[String]): Map[String, Int] =
    words.groupBy(_.toString).map { case (key, values) => (key, values.size) }

  private[wordcount] def extractWords(text: String): List[String] =
    text
      .replace(",", "")
      .replace("-", " ")
      .replace("\"", "")
      .replace(".", "")
      .toLowerCase
      .split("\\s+")
      .filter(_.nonEmpty)
      .toList
}
