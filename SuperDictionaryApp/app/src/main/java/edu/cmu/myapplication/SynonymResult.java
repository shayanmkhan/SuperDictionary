package edu.cmu.myapplication;

import java.util.List;

public class SynonymResult extends DictionaryResult {
    private List<String> synonyms;

    public List<String> getSynonyms() {
        return synonyms;
    }

    public void setSynonyms(List<String> synonyms) {
        this.synonyms = synonyms;
    }
}
