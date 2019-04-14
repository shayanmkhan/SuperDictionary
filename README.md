# SuperDictionary
## Description
This project was an exercise in building distributed applications. The main components of the application are the following:
1. A native Android front-end, which makes asynchronous calls to the web service
2. A Java EE web service that handles requests, logs information, performs analytics, and generates a web dashboard
3. A MongoDB database that stores usage information; this information is analyzed by the web service
Below is a screenshot demonstrating the design of the application:
![alt text](https://imgur.com/vozYYOF)

## How to Use
This repository contains all original source code. The servlet was built using Netbeans IDE, and the Android app was built using Android Studio. To run either program, simply import into the respective IDE and run the project.

The web service has been packaged into a Docker container and reployed to the cloud using Heroku for always-on service. Its web dashboard can be accessed at https://still-brook-30504.herokuapp.com/SuperDictionary/dashboard
