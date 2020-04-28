package io.thoughtworks.wordcount

import org.specs2.mutable.Specification
import org.specs2.specification.AllExpectations

class WordCountSpec extends Specification with AllExpectations {
  "word count".title

  val wordcount = new WordCount

  "should extract words" >> {
    "from empty string" >> {
      wordcount.extractWords(" ") should_== List.empty[String]
      wordcount.extractWords("") should_== List.empty[String]
    }

    "from normal words " >> {
      wordcount.extractWords("on monday") should_== List("on", "monday")
    }

    "to lowercase" >> {
      wordcount.extractWords("On Monday") should_== List("on", "monday")
    }

    "ignore special character" >> {
      wordcount.extractWords("On, - \"Monday\" 2,000.") should_== List("on", "monday", "2000")
    }

    "from multi line" >> {
      wordcount.extractWords("On \n\n Monday \n 2,000") should_== List("on", "monday", "2000")
    }

  }

  "should count words" >> {
    wordcount.countWords(List("on", "monday", "on")) should_== Map("on" -> 2, "monday" -> 1)
  }

  "should list word frequency in order" >> {
    wordcount.listWords("on monday on") should_== List(
      WordFrequency("monday", 1),
      WordFrequency("on", 2)
    )
    wordcount.listWords("on monday on monday monday") should_== List(
      WordFrequency("on", 2),
      WordFrequency("monday", 3)
    )
  }
}
