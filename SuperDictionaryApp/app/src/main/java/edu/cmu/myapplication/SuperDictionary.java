package edu.cmu.myapplication;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.method.ScrollingMovementMethod;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;

import java.util.List;

//Handles all of the UI processes
public class SuperDictionary extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Populate the dropdown menu
        //Code taken from https://developer.android.com/guide/topics/ui/controls/spinner
        Spinner menu = findViewById(R.id.menu);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
                R.array.menu, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        menu.setAdapter(adapter);

        //Create button listener
        Button submitButton = findViewById(R.id.submitButton);
        submitButton.setOnClickListener((click) -> {
            //Extract parameters from user input
            EditText textInput = findViewById(R.id.word);
            String word = textInput.getText().toString();
            String operation = menu.getSelectedItem().toString().toLowerCase();

            //Send parameters to HTTP client for processing
            DictionaryClient client = new DictionaryClient();
            client.getData(word, operation, this);
        });
    }

    //Displays the results of the web service call
    void displayResults(DictionaryResult result) {
        //Setup
        TextView resultsView = findViewById(R.id.resultsView);
        resultsView.setMovementMethod(new ScrollingMovementMethod());
        StringBuilder sb = new StringBuilder();

        //Display results differently based on the type of dictionary operation
        switch(result.getType()) {
            case "definitions":
                List<DefinitionResult.Definition> definitions = ((DefinitionResult) result).getDefinitions();

                //Check if result list is empty
                if(definitions.size() == 0) {
                    sb.append("No definitions for " + result.getWord() + " found!");
                    break;
                }

                //Build output string
                sb.append("Definitions of " + result.getWord() + ":\n");
                for(DefinitionResult.Definition def : definitions) {
                    sb.append(def.getPartOfSpeech() + ": \"" + def.getDefinition() + "\"\n");
                }
                break;
            case "synonyms":
                List<String> synonyms = ((SynonymResult) result).getSynonyms();

                //Check if result list is empty
                if(synonyms.size() == 0) {
                    sb.append("No synonyms for " + result.getWord() + " found!");
                    break;
                }

                //Build output string
                sb.append("Synonyms of " + result.getWord() + ":\n");
                buildOutputString(sb, synonyms);
                break;
            case "antonyms":
                List<String> antonyms = ((AntonymResult) result).getAntonyms();

                //Check if result list is empty
                if(antonyms.size() == 0) {
                    sb.append("No antonyms for " + result.getWord() + " found!");
                    break;
                }

                //Build output string
                sb.append("Antonyms of " + result.getWord() + ":\n");
                buildOutputString(sb, antonyms);
                break;
            case "examples":
                List<String> examples = ((ExampleResult) result).getExamples();

                //Check if result list is empty
                if(examples.size() == 0) {
                    sb.append("No examples for " + result.getWord() + " found!");
                    break;
                }

                //Build output string
                sb.append("Examples of " + result.getWord() + ":\n");
                for(String example : examples) {
                    sb.append("\"" + example + "\"\n");
                }
                break;
            case "rhymes":
                List<String> rhymes = ((RhymeResult) result).getRhymes().getAll();

                //Check if result list is empty
                if(rhymes.size() == 0) {
                    sb.append("No rhymes for " + result.getWord() + " found!");
                    break;
                }

                //Build output string
                sb.append("Words that rhyme with " + result.getWord() + ":\n");
                buildOutputString(sb, rhymes);
                break;
        }

        //Display output in results textView
        resultsView.setText(sb.toString());
    }

    //Given a list of strings, builds an output string that is separated by newline characters
    void buildOutputString(StringBuilder sb, List<String> results) {
        for(String result : results) {
            sb.append(result + "\n");
        }
    }
}
