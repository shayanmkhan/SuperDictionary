package edu.cmu.myapplication;

import android.os.AsyncTask;
import com.google.gson.Gson;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

//Handles all communication with web service
public class DictionaryClient {
    //Reference to UI object
    SuperDictionary userInterface;

    //Creates object for processing information asynchronously
    public void getData(String word, String operation, SuperDictionary userInterface) {
        this.userInterface = userInterface;
        new DataRetriever().execute(word, operation);
    }

    //Makes an asynchronous server call to retrieve the desired information
    private class DataRetriever extends AsyncTask<String, Void, DictionaryResult> {
        @Override
        //Process in background
        protected DictionaryResult doInBackground(String... input) {
            return getData(input[0], input[1]);
        }
        @Override
        //Make callback to UI thread
        protected void onPostExecute(DictionaryResult result) {
            userInterface.displayResults(result);
        }

        // Makes an HTTP GET request with the given parameters
        private DictionaryResult getData(String word, String operation) {

            // Setup
            String response = "";
            HttpURLConnection conn;
            int status = 0;

            try {
                // Pass the parameters on the URL line
                URL url = new URL("https://still-brook-30504.herokuapp.com/SuperDictionary/"
                        + word + "/" + operation);
                conn = (HttpURLConnection) url.openConnection();
                conn.setRequestMethod("GET");

                // Tell the server what format we want back
                conn.setRequestProperty("Accept", "text/plain");

                // Wait for response
                status = conn.getResponseCode();

                // If things went poorly, don't try to read any response, just return.
                if (status != 200) {
                    return null;
                }

                // Read in response and save as a string
                String output;
                BufferedReader br = new BufferedReader(new InputStreamReader((
                        conn.getInputStream())));
                StringBuilder sb = new StringBuilder();
                while ((output = br.readLine()) != null) {
                    sb.append(output + "\n");
                }
                response = sb.toString();

                // Disconnect from server
                conn.disconnect();

            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }

            //Parse and return result
            DictionaryResult result = parseResponse(response, operation);
            return result;
        }

        //Takes a json response string and a dictionary operation name and generates a DictionaryResult object
        private DictionaryResult parseResponse(String jsonResponse, String operation) {
            //Setup
            Gson parser = new Gson();
            DictionaryResult result;

            //Build result object based on given dictionary operation
            switch(operation) {
                case "definitions":
                    result = parser.fromJson(jsonResponse, DefinitionResult.class);
                    break;
                case "synonyms":
                    result = parser.fromJson(jsonResponse, SynonymResult.class);
                    break;
                case "antonyms":
                    result = parser.fromJson(jsonResponse, AntonymResult.class);
                    break;
                case "examples":
                    result = parser.fromJson(jsonResponse, ExampleResult.class);
                    break;
                default:
                    result = parser.fromJson(jsonResponse, RhymeResult.class);
                    break;
            }

            //Store type of dictionary operation and return
            result.setType(operation);
            return result;
        }

    }
}
