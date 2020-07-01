package io.thoughtworks.wordcount

import scala.io.Source
import java.net.URL
import java.io.File

case class InputSource(source: String, path: String)

object EnvParser {
  def parse(input: String): Either[Throwable, InputSource] = {
    val inputList: List[String] = input.split("@").toList.map(_.trim)

    inputList match {
      case source :: path :: Nil => Right(InputSource(source, path))
      case _ => Left(new Error(s"Invalid source: ${input}"))
    }
  }

  def getContentFromSource(source: InputSource): Either[Throwable, String] = {
    source match {
      case InputSource("URL", link) => Right(Source.fromURL(new URL(link)).getLines().mkString("\n"))
      case InputSource("FILE", path) => Right(Source.fromFile(new File(path)).getLines().mkString("\n"))
      case InputSource(unknown, path) => Left(new Error(s"Unsupported source ${unknown}: ${path}"))
    }
  }

}
