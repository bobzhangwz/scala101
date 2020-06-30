package io.thoughtworks.wordcount

import scala.io.Source
import java.net.URL
import java.io.File

case class InputSource(source: String, path: String)

object EnvParser {
  def parse(input: String): InputSource = {
    val inputList: List[String] = input.split("@").toList.map(_.trim)

    inputList match {
      case source :: path :: Nil => InputSource(source, path)
      case _ => throw new Error(s"Invalid source: ${input}")
    }
  }

  def getContentFromSource(source: InputSource): String = {
    source match {
      case InputSource("URL", link) => Source.fromURL(new URL(link)).getLines().mkString("\n")
      case InputSource("FILE", path) => Source.fromFile(new File(path)).getLines().mkString("\n")
      case InputSource(unknown, path) => throw new Error(s"Unsupported source ${unknown}: ${path}")
    }
  }

}
