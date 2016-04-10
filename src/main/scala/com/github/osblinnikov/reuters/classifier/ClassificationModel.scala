package com.github.osblinnikov.reuters.classifier

import org.apache.spark.mllib.classification.NaiveBayesModel

/**
  * Created by oleg on 4/9/16.
  */
case class ClassificationModel(model: NaiveBayesModel, idf: Map[String, Double], vocabulary: Seq[String], topics: Seq[String])
