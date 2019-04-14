package edu.cmu.myapplication;

import java.util.List;

public class ExampleResult extends DictionaryResult{
    private List<String> examples;

    public List<String> getExamples() {
        return examples;
    }

    public void setExamples(List<String> examples) {
        this.examples = examples;
    }
}
