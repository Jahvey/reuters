package com.reuters.classifier.sub;

import org.apache.commons.cli.*;

import java.io.PrintWriter;
import java.io.StringWriter;

/**
 * Parsing Console Arguments and return the current value
 */
public class ConsoleParameters {

    @Override
    public int hashCode(){
        return hash_code;
    }

    @Override
    public boolean equals(Object arg0) {
        return hash_str.equals(((ConsoleParameters)arg0).hash_str);
    }

    private void setHashCode(Option[] options){
        StringBuilder builder = new StringBuilder();
        for(Option opt: options){
            if(opt.getOpt().equals("maxItemLabels")
            || opt.getOpt().equals("withEmptyTopic")
            || opt.getOpt().equals("src")
            || opt.getOpt().equals("spark")
            ){
                continue;
            }
            builder.append("-" + opt.getOpt() + " " + opt.getValue());
            builder.append(" ");
        }
        hash_str = builder.toString();
        hash_code = hash_str.hashCode();
    }
    private String hash_str = "";
    private int hash_code = -1;

    int maxLabelsForItem = Integer.MAX_VALUE;
    boolean allowEmptyTopic = false;
    boolean printConfusionMatrix = false;
    String srcFolder = "reuters";
    String sparkURL = "local[8]";

    public ConsoleParameters(String[] args) throws ParseException {
        Options options = new Options();
        
        options.addOption("maxItemLabels", true, "max number of labels, default="+maxLabelsForItem);
        options.addOption("withEmptyTopic", false, "allow to classify empty topic, default="+allowEmptyTopic);
        options.addOption("printConfusionMatrix", false, "output confusion matrix on the screen, default="+printConfusionMatrix);
        options.addOption("src", true, "source folder, default=" + srcFolder);
        options.addOption("spark", true, "URL for spark framework, default=" + sparkURL);
        options.addOption("h", false, "print this help");

        boolean helpRequired;
        CommandLine cmd = null;
        try {
            cmd = new PosixParser().parse(options, args);
            helpRequired = cmd.hasOption("-h") || cmd.hasOption("-help");
        }catch (ParseException e) {
            helpRequired = true;
        }
        if (helpRequired) {
            HelpFormatter formatter = new HelpFormatter();
            StringWriter sw = new StringWriter();
            PrintWriter pw = new PrintWriter(sw);
            formatter.printHelp(
                    pw,
                    80,
                    "Reuters-Classifier",
                    "",
                    options,
                    4,
                    0,
                    "",
                    true);
            throw new ParseException(sw.getBuffer().toString());
        }

        this.maxLabelsForItem = cmd.getOptionValue("maxItemLabels") != null ? Integer.parseInt(cmd.getOptionValue("maxItemLabels")) : maxLabelsForItem;
        this.allowEmptyTopic = cmd.hasOption("withEmptyTopic");
        this.printConfusionMatrix = cmd.hasOption("printConfusionMatrix");
        this.srcFolder = cmd.getOptionValue("src") != null ? cmd.getOptionValue("src") : srcFolder;
        this.sparkURL = cmd.getOptionValue("spark") != null ? cmd.getOptionValue("spark") : sparkURL;
        this.setHashCode(cmd.getOptions());
    }
}
