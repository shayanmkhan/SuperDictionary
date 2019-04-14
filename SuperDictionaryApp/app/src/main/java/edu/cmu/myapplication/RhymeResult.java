package edu.cmu.myapplication;

import java.util.List;

public class RhymeResult extends DictionaryResult {
    private RhymeList rhymes;

    public RhymeList getRhymes() {
        return rhymes;
    }

    public void setRhymes(RhymeList rhymes) {
        this.rhymes = rhymes;
    }

    public class RhymeList {
        private List<String> all;

        public List<String> getAll() {
            return all;
        }

        public void setAll(List<String> all) {
            this.all = all;
        }
    }
}
