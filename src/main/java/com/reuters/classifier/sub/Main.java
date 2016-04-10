package com.reuters.classifier.sub;

import com.reuters.classifier.ClassificationModel;
import com.reuters.classifier.Classifier;
import org.apache.commons.cli.ParseException;
import org.apache.spark.SparkConf;
import org.apache.spark.SparkContext;
import org.apache.spark.mllib.evaluation.MulticlassMetrics;

import java.io.IOException;
import java.util.Arrays;

/**
 * Created by oleg on 4/9/16.
 */
public class Main {
    public static void main(String[] args) throws IOException {
        System.out.println(Arrays.toString(args));
        ConsoleParameters parameters;
        try {
            System.out.println("Try more options, type -h for help");
            parameters = new ConsoleParameters(args);
        } catch( ParseException exp ) {
            System.out.println(exp.getMessage());
            return;
        }

        System.out.println("XML parsing ...");
        ExtractReutersWithTopic reuters = new ExtractReutersWithTopic(
                parameters.srcFolder,
                parameters.maxLabelsForItem,
                parameters.allowEmptyTopic);

        reuters.extract();

        System.out.println("Spark init ...");
        SparkContext sc = new SparkContext(new SparkConf().setAppName(parameters.srcFolder+"_naive"));

        System.out.println("Training ...");
        ClassificationModel model = Classifier.train(sc, reuters.getTrainingSet());

        System.out.println("Evaluation ...");
        MulticlassMetrics metrics = Classifier.evaluate(sc, reuters.getTestSet(), model);

        System.out.println("---------------------------");
        if(parameters.printConfusionMatrix) {
            System.out.println("confusionMatrix:");
            System.out.println(metrics.confusionMatrix().toString(Integer.MAX_VALUE, Integer.MAX_VALUE));
        }
        System.out.println("fMeasure: "+metrics.fMeasure());
        System.out.println("precision: " + metrics.precision()+" recall: "+metrics.recall());
        System.out.println("---------------------------");
    }
}
