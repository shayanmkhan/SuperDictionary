package edu.cmu.myapplication;

import java.util.List;

public class AntonymResult extends DictionaryResult {
    private List<String> antonyms;

    public List<String> getAntonyms() {
        return antonyms;
    }

    public void setAntonyms(List<String> antonyms) {
        this.antonyms = antonyms;
    }
}
