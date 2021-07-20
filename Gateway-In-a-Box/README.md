# ActionLogger - Webapplication Integration

This is a guide to assist in integrating web based applicationsa with action logger to track the activities. Gatway In a Box is a portable framework for bulding web portals. To track any activity and web based applications the following two steps are required.

- Get the token to log the data
- log the activity with applicatoin name and optional metric name

To integrate actionlogger with Gateway-In-a-Box, three methods are added to each required controller in [controller](./src/main/java/com/ipt/web/controller) directory. Each function is described below:

1. getToken() - gets the token from eager to add log 

Note: TOKEN_URL needs to be defined according to the URL of eager app

2. log() - logs the usage of application without any parameter meaning some accessed the application in general

3. log() - logs the usage of application with metric name to provide advanced tracking like access to a particular page or a specific action 

Note: For 2 & 3, LOG_URL needs to be defined according to the URL of eager app, Name of the application and metric needs to be updated as desired