package com.github.osblinnikov.reuters.classifier;


import org.apache.lucene.benchmark.utils.ExtractReuters;

import java.io.BufferedReader;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.FileSystems;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by oleg on 4/9/16.
 */
public class ExtractReutersWithTopic extends ExtractReuters {

    private static String[] META_CHARS = { "&", "<", ">", "\"", "'" };

    private static String[] META_CHARS_SERIALIZATIONS = { "&amp;", "&lt;",
            "&gt;", "&quot;", "&apos;" };
    private final int limitTopics;
    private final boolean allowEmptyTopic;

    private List<Document> trainingSet = new LinkedList<>();

    private List<Document> testSet = new LinkedList<>();


    private Pattern EXTRACTION_PATTERN = Pattern
            .compile("<REUTERS (.*?)>|<TITLE>(.*?)</TITLE>|<TOPICS>(.*?)</TOPICS>|<BODY>(.*?)</BODY>");

    public ExtractReutersWithTopic(String reutersDir, int limitTopics, boolean allowEmptyTopic) throws IOException {
        super(FileSystems.getDefault().getPath(reutersDir), FileSystems.getDefault().getPath("tmp"));
        this.limitTopics = limitTopics;
        this.allowEmptyTopic = allowEmptyTopic;
    }

    public ExtractReutersWithTopic(String reutersDir, boolean allowEmptyTopic) throws IOException {
        this(reutersDir, Integer.MAX_VALUE, allowEmptyTopic);
    }

    @Override
    protected void extractFile(Path sgmFile) {
        try (BufferedReader reader = Files.newBufferedReader(sgmFile, StandardCharsets.ISO_8859_1)) {
            StringBuilder buffer = new StringBuilder(1024);
            ArrayList<String> outBuffer = new ArrayList<>(10);

            String line = null;
            while ((line = reader.readLine()) != null) {
                // when we see a closing reuters tag, flush the file

                if (line.indexOf("</REUTERS") == -1) {
                    // Replace the SGM escape sequences

                    buffer.append(line).append(' ');// accumulate the strings for now,
                    // then apply regular expression to
                    // get the pieces,
                } else {
                    // Extract the relevant pieces and write to a file in the output dir
                    Matcher matcher = EXTRACTION_PATTERN.matcher(buffer);
                    String topics = "";
                    String title = "";
                    String body = "";
                    String reuters = "";
                    while (matcher.find()) {
                        for (int i = 1; i <= matcher.groupCount(); i++) {
                            if (matcher.group(i) != null) {
                                String text = matcher.group(i).replaceAll(META_CHARS_SERIALIZATIONS[i], META_CHARS[i]);
                                if(matcher.group(0).startsWith("<TOPICS>")){
                                    topics = text.replaceAll("</D>","").replaceFirst("<D>","");
                                }else if(matcher.group(0).startsWith("<TITLE>")){
                                    title = text;
                                }else if(matcher.group(0).startsWith("<BODY>")){
                                    body = text;
                                }else if(matcher.group(0).startsWith("<REUTERS ")){
                                    reuters = text;
                                }
                            }
                        }
                    }
                    String lewissplit = "TEST";
                    for(String r : reuters.split(" ")){
                        if(r.startsWith("LEWISSPLIT")){
                            lewissplit = r.replace("\"","").replace("LEWISSPLIT=","");
                            break;
                        }
                    }

                    String[] splt = topics.split("<D>");
                    List<String> spltLimited = new ArrayList<>(splt.length);
                    for(int i=0; i<splt.length && i<limitTopics; i++){
                        spltLimited.add(splt[i]);
                    }
                    if(allowEmptyTopic || !spltLimited.get(0).isEmpty()) {
                        if (lewissplit.equals("TEST")) {
                            testSet.add(new Document(title, body, spltLimited));
                        } else {
                            trainingSet.add(new Document(title, body, spltLimited));
                        }
                    }

                    buffer.setLength(0);
                }
            }
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }


    public List<Document> getTrainingSet() {
        return trainingSet;
    }

    public List<Document> getTestSet() {
        return testSet;
    }
}
