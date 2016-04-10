package com.github.osblinnikov.reuters.classifier

import org.apache.spark.rdd.RDD

/**
  * Created by oleg on 4/9/16.
  */
object TFIDF {
  def mergeTFIDF(tf: RDD[(DocumentWithTerms, Seq[(String, Double)])], localIDF: Map[String, Double]) = {
    tf.map({case(doc, terms) =>
      (doc, terms.map {case(term, tfVal) => (term, localIDF.getOrElse(term, 1.0) * tfVal)})
    })
  }

  def getTF (termDocsRdd: RDD[DocumentWithTerms]) : RDD[(DocumentWithTerms, Seq[(String, Double)])] = {
    termDocsRdd.map( doc => {
      val bodyTermsSet = doc.bodyTerms.toSet
      val anyTermFrequency = bodyTermsSet.map(anyTerm => doc.bodyTerms.count(_ == anyTerm))
      val anyTermMax = if(anyTermFrequency.isEmpty) 1 else anyTermFrequency.max
      (doc, bodyTermsSet.map(term => {
        (term, 0.5 + 0.5 * doc.bodyTerms.count(_ == term).toDouble/anyTermMax.toDouble)
      }).toSeq.sortBy {case(term, tfVal) => -tfVal})
    })
  }

  def getIDF(termDocsRdd: RDD[DocumentWithTerms]) : RDD[(String, Double)] = {
    val totalNumberOfDocuments = termDocsRdd.count()
    val countsForTerms = termDocsRdd.flatMap(_.bodyTerms.distinct.map(term => (term,  1))).reduceByKey((a, b) => a + b)
    countsForTerms.map {case(term, count) => (term, Math.log(totalNumberOfDocuments.toDouble/count.toDouble))}
  }
}
