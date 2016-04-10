package com.github.osblinnikov.reuters.classifier;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Created by oleg on 4/9/16.
 */
public class Document {
    Document(String title, String body, List<String> topics){
        this.title = title;
        this.body = body;
        this.topics.addAll(topics);
    }
    public String title;
    public String body;
    public Set<String> topics = new HashSet<>();
}
