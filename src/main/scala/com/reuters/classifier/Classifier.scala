package com.reuters.classifier

import com.reuters.classifier.sub.Document
import org.apache.spark.SparkContext
import org.apache.spark.mllib.classification.NaiveBayes
import org.apache.spark.mllib.evaluation.MulticlassMetrics
import org.apache.spark.mllib.linalg.Vectors
import org.apache.spark.mllib.regression.LabeledPoint
import org.apache.spark.rdd.RDD
/**
  * Created by oleg on 4/9/16.
  */
object Classifier {
  def evaluate(sc: SparkContext, testSetInput: java.util.List[Document], model: ClassificationModel): MulticlassMetrics = {
    val tfForTestDocs = TFIDF.getTF(sc.parallelize[DocumentWithTerms](Tokenizer.tokenizeAll(testSetInput)))
    // An assumption that test document only little bit changes words statistics, can be fixed by iteration over testDocs
    //      val idfForDocs = TFIDF.getIDF(testDocAndTrainSet)
    val tfIdfForTestDocs = TFIDF.mergeTFIDF(tfForTestDocs, model.idf)
    val testSet = convertToMLLibRepresentation(tfIdfForTestDocs, model.vocabulary, model.topics)
    val allPredictions = testSet.map { labeledPoint =>
      (model.model.predict(labeledPoint.features), labeledPoint.label)
    }

    new MulticlassMetrics(allPredictions)
  }

  def train(sc: SparkContext, trainingSetInput : java.util.List[Document]): ClassificationModel = {
    val tokenizedTrainDocs = Tokenizer.tokenizeAll(trainingSetInput)
    val trainDocuments = sc.parallelize[DocumentWithTerms](tokenizedTrainDocs)
    val vocabulary = trainDocuments.flatMap(_.bodyTerms).distinct().sortBy(identity).collect()
    val topics = trainDocuments.flatMap(_.topics).distinct().collect()

    val tfForTrainDocs = TFIDF.getTF(trainDocuments)
    val idfForTrainDocs = TFIDF.getIDF(trainDocuments).collect()
    val idfForTrainDocsMap = idfForTrainDocs.groupBy(_._1).map{case (k,v) => (k,v.head._2)}
    val tfIdfTrainDocs = TFIDF.mergeTFIDF(tfForTrainDocs, idfForTrainDocsMap)

    val trainingSet = convertToMLLibRepresentation(tfIdfTrainDocs, vocabulary, topics)

    ClassificationModel(
      NaiveBayes.train(trainingSet, lambda = 1.0, modelType = "multinomial"),
      idfForTrainDocsMap,
      vocabulary,
      topics)
  }

  def createFeatureVector(termSeq: Seq[(String, Double)], vocabulary: Seq[String], topics: Seq[String])={
    val termPairs = termSeq.map(idfs => (vocabulary.indexOf(idfs._1), idfs._2)).filter(_._1 >= 0)
    Vectors.sparse(vocabulary.length, termPairs)
  }

  def convertToMLLibRepresentation(tfIdfDocs: RDD[(DocumentWithTerms, Seq[(String, Double)])], vocabulary: Seq[String], topics: Seq[String]): RDD[LabeledPoint] = {
    tfIdfDocs.flatMap { termDocAndSeq =>
      val sparseVector = createFeatureVector(termDocAndSeq._2, vocabulary, topics)
      termDocAndSeq._1.topics.map { label =>
        LabeledPoint(topics.indexOf(label).toDouble, sparseVector)
      }
    }
  }
}
