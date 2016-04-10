package com.github.osblinnikov.reuters.classifier

/**
  * Created by oleg on 4/9/16.
  */
case class DocumentWithTerms(title: String, topics: scala.collection.mutable.Set[String], bodyTerms: Seq[String])
