package com.reuters.classifier

import java.io.StringReader

import com.reuters.classifier.sub.Document
import org.apache.lucene.analysis.en.EnglishAnalyzer
import org.apache.lucene.analysis.tokenattributes.CharTermAttribute

import scala.collection.JavaConversions._
import scala.collection.mutable

object Tokenizer {

  def tokenizeAll(docs: java.util.List[Document]) = {
    asScalaBuffer(docs).map((doc: Document) => DocumentWithTerms(doc.title, asScalaSet(doc.topics), tokenize(doc.title+"\n"+doc.body)))
  }

  def tokenize(content: String): Seq[String] = {
    val tReader = new StringReader(content)
    val analyzer = new EnglishAnalyzer()
    val tStream = analyzer.tokenStream("contents", tReader)
    val term = tStream.addAttribute(classOf[CharTermAttribute])
    tStream.reset()

    val result = mutable.ArrayBuffer.empty[String]
    while (tStream.incrementToken()) {
      val termValue = term.toString
      if (!(termValue matches ".*[\\d\\.].*")) {
        result += term.toString
      }
    }
    result
  }
}

