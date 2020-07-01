package io.thoughtworks.wordcount

import org.specs2.mutable.Specification
import org.specs2.specification.AllExpectations
import java.io.PrintWriter

class EnvParserTest extends Specification with AllExpectations {

  "EnvParser".title

  "should parse env input to input source" >> {
    "from normal String" >> {
      EnvParser.parse("FILE@/work/round") should beRight(InputSource("FILE", "/work/round"))
      EnvParser.parse("URL@http://www.baidu.com") should beRight(InputSource("URL", "http://www.baidu.com"))
    }

    "from bad String" >> {
      EnvParser.parse(":URL@/work/round") should beLeft
      EnvParser.parse("URL@/work/round@/abcdef") should beLeft
    }
  }

  "should get content" >> {
    "from FILE source" >> {
      new PrintWriter("/tmp/helloworld.txt") { write("hello world"); close }
      EnvParser.getContentFromSource(InputSource("FILE", "/tmp/helloworld.txt")) should_== beRight("hello world")
    }
    "from url source" >> {
      val url = "https://github.com/bobzhangwz/scala101/raw/workshop03/src/test/scala/resources/helloworld.txt"
      EnvParser.getContentFromSource(InputSource("URL", url)) should_== beRight("hello world")
    }
  }
}
