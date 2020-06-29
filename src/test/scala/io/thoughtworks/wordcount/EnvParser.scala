package io.thoughtworks.wordcount

import org.specs2.mutable.Specification
import org.specs2.specification.AllExpectations
import java.io.PrintWriter

class EnvParserTest extends Specification with AllExpectations {

  "EnvParser".title

  "should parse env input to input source" >> {
    "from normal String" >> {
      EnvParser.parse("FILE:/work/round") should_== InputSource("FILE", "/work/round")
      EnvParser.parse("URL:/work/round") should_== InputSource("URL", "/work/round")
    }

    "from bad String" >> {
      EnvParser.parse(":URL:/work/round") must throwA[Error]
      EnvParser.parse("URL:/work/round:/abcdef") must throwA[Error]
    }
  }

  "should get content" >> {
    "from FILE source" >> {
      new PrintWriter("/tmp/helloworld.txt") { write("hello world"); close }
      EnvParser.getContentFromSource(InputSource("FILE", "/tmp/helloworld.txt")) should_== "hello world"
    }
    "from url source" >> {
      val url =  "https://raw.githubusercontent.com/bobzhangwz/scala101/master/src/test/resources/helloworld.txt"
      EnvParser.getContentFromSource(InputSource("URL", url)) should_== "hello world"
    }
  }
}
