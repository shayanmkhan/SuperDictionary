// Author: Shayan Khan

// Description: A RESTful web service which receives an dictionary request from a client, calls a dictionary API,
// and responds with a JSON string with the requested information. Also logs interactions in a database, performs 
// analytics on that data, and displays the results in a web dashboard

package edu.cmu.superdictionary;

import com.mashape.unirest.http.HttpResponse;
import com.mashape.unirest.http.Unirest;
import com.mashape.unirest.http.exceptions.UnirestException;
import com.mongodb.MongoClient;
import com.mongodb.MongoClientURI;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import com.mongodb.client.MongoIterable;
import java.io.IOException;
import java.io.PrintWriter;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.bson.Document;

@WebServlet(name = "SuperDictionary", urlPatterns = {"/SuperDictionary/*"})
public class SuperDictionary extends HttpServlet {

    @Override
    // Handles the HTTP GET method
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Store request parameters
        String[] requestParameters = request.getPathInfo().substring(1).split("/");

        // Execute if dashboard is requested
        if(requestParameters[0].equals("dashboard")) {
            // Connect to database
            MongoCollection collection = connectToDB();
            
            // Run analytics on database contents
            String topWord = findTopWord(collection);
            String topOperation = findTopOperation(collection);   
            long avgResponseTime = calcAvgResponseTime(collection);
            String logs = getLogs(collection);
            
            // Store results in request object
            request.setAttribute("topWord", topWord);
            request.setAttribute("topOperation", topOperation);
            request.setAttribute("avgResponseTime", avgResponseTime);
            request.setAttribute("logs", logs);
            
            // Display results in JSP page
            RequestDispatcher dashboardView = request.getRequestDispatcher("/dashboard.jsp");
            dashboardView.forward(request, response);
        }   

        // Execute if dictionary service is requested
        else if (requestParameters.length == 2) {
            // Start timer
            long startTime = System.currentTimeMillis();
            
            // Parse request
            String word = requestParameters[0].toLowerCase();
            String operation = requestParameters[1].toLowerCase();

            // Retrieve data from web API using given parameters, and save as a JSON string
            // Driver code taken from WordsAPI SDK
            HttpResponse apiResponse = null;
            String apiRequest = "https:// wordsapiv1.p.rapidapi.com/words/" + word + "/" + operation;
            try {
                apiResponse = Unirest.get(apiRequest)
                        .header("X-RapidAPI-Key", "93eff8fef7msh7e7d4e83109205fp1b5f9djsn9e3712d5c883")
                        .asJson();
            } catch (UnirestException ex) {
                Logger.getLogger(SuperDictionary.class.getName()).log(Level.SEVERE, null, ex);
            }
            String data = apiResponse.getBody().toString();

            // Send fetched data to client
            response.setStatus(200);
            PrintWriter out = response.getWriter();
            out.print(data);
            
            // End timer and save value
            Long responseTime = System.currentTimeMillis() - startTime;
            
            // Log request and response info in database
            logInfo(request, apiRequest, response, responseTime);
        }

    }
    
    // Stores the given information in a MongoDB database
    void logInfo(HttpServletRequest request, String apiRequest, HttpServletResponse response, Long responseTime) {
        // Extract various information from parameters
        String[] requestParams = request.getPathInfo().substring(1).split("/");
        String userInfo = request.getHeader("User-Agent");
        String requestWord = requestParams[0];
        String requestOperation = requestParams[1];
        DateFormat df = new SimpleDateFormat("MM/dd/yy HH:mm:ss");
        String timeOfRequest = df.format(new Date());
      
        // Create map to represent data
        Map<String,Object> logData = new LinkedHashMap<>();
        logData.put("Time of Request", timeOfRequest);
        logData.put("User Info", userInfo);
        logData.put("Requested Word", requestWord);
        logData.put("Requested Operation", requestOperation);
        logData.put("API Request", apiRequest);
        logData.put("Response Time (ms)", responseTime);
        
        // Connect to database
        MongoCollection collection = connectToDB();
        
        // Insert data into database
        Document databaseEntry = new Document(logData);
        collection.insertOne(databaseEntry);
    }
    
    // Connects to MongoDB and returns a referenece to the Mongo Collection
    // Driver code taken from MongoDB website
    MongoCollection connectToDB() {
        System.out.println("Connecting to database...");
        MongoClientURI uri = new MongoClientURI(
                "mongodb:// shayankhan:qweqwe@cluster0-shard-00-00-cb8fp.mongodb.net:27017,cluster0-shard-00-01-cb8fp.mongodb.net:27017,cluster0-shard-00-02-cb8fp.mongodb.net:27017/test?ssl=true&replicaSet=Cluster0-shard-0&authSource=admin&retryWrites=true");
        MongoClient mongoClient = new MongoClient(uri);
        MongoDatabase database = mongoClient.getDatabase("SuperDictionary");
        MongoCollection collection = database.getCollection("Usage Logs");
        System.out.println("Connection successful.");
        return collection;
    }
    
    // Finds the most commonly requested word in the Mongo collection
    String findTopWord(MongoCollection collection) {
        // Setup
        MongoIterable<Document> words = collection.find();
        Map<String, Integer> wordFreq = new HashMap<>();
        int maxFreq = 0;
        String topWord = "";
        
        // Count the frequency of each word
        for (Document d : words) {
            String word = d.getString("Requested Word");
            if(!wordFreq.containsKey(word)) {
                wordFreq.put(word, 1);
            }
            else {
                wordFreq.put(word, wordFreq.get(word) + 1);
            }
        }
        
        // Calculate the highest word frequency
        for(Integer freq : wordFreq.values()) {
            if(freq > maxFreq) {
                maxFreq = freq;
            }
        }
        
        // Find the word corresponding to the highest frequency
        for(String word : wordFreq.keySet()) {
            if(wordFreq.get(word) == maxFreq) {
                topWord = word;
            }
        }
        
        return topWord;
    }

    // Finds the most commonly requested operation in the Mongo collection
    String findTopOperation(MongoCollection collection) {
        // Setup
        MongoIterable<Document> words = collection.find();
        Map<String, Integer> operationFreq = new HashMap<>();
        int maxFreq = 0;
        String topOperation = "";
        
        // Count the frequency of each operation
        for (Document d : words) {
            String word = d.getString("Requested Operation");
            if(!operationFreq.containsKey(word)) {
                operationFreq.put(word, 1);
            }
            else {
                operationFreq.put(word, operationFreq.get(word) + 1);
            }
        }
        
        // Calculate the highest operation frequency
        for(Integer freq : operationFreq.values()) {
            if(freq > maxFreq) {
                maxFreq = freq;
            }
        }
        
        // Find the operation corresponding to the highest frequency
        for(String word : operationFreq.keySet()) {
            if(operationFreq.get(word) == maxFreq) {
                topOperation = word;
            }
        }
        
        return topOperation;
    }
    
    // Calculates the average response time in the Mongo collection
    long calcAvgResponseTime(MongoCollection collection) {
        // Setup
        MongoIterable<Document> documents = collection.find();
        long sum = 0;
        int count = 0;
        
        // Calculate number and sum of all response times
        for(Document doc : documents) {
            Long responseTime = doc.getLong("Response Time (ms)");
            sum += responseTime;
            count++;
        }
        
        // Return average
        return (sum / count);
    }
    
    // Returns a string representation of all logs in Mongo collection
    String getLogs(MongoCollection collection) {
        // Setup
        MongoIterable<Document> documents = collection.find();
        StringBuilder sb = new StringBuilder();
        
        // Append the contents of all logs
        for(Document doc : documents) {
            for(String key : doc.keySet()) {
                sb.append(key + ": " + doc.get(key) + "<br>"); 
            }
            sb.append("<br>");  
        }
        
        return sb.toString();
    }

}
