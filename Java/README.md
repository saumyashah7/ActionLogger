# ActionLogger - Java

This library is developed to handle client side operations for java applications. It comes in handy for desktop applications since the web applications can log activities via GET and POST requests. To log the activities in any java project please follow the steps listed below:

- Clone the repository
```
git clone https://github.com/saumyashah7/ActionLogger.git
```

- Add the libraries listed in ActionLogger/Java/JAR_files or [here](https://github.com/saumyashah7/ActionLogger/tree/master/Java/JAR_files) to the build path of the project
- Import the action logger and exception libraries in each file that needs to log activity
```
import com.java.actionlogger.ActionLogger;
import java.io.IOException;
import org.json.simple.parser.ParseException;
import com.java.actionloggerexception.CryptoException;
```


- Define the log with following parameters
	- application_name(required) -> name of the application
	- metric_name(optional) -> name of the metric
```
ActionLogger.log("application_name,metric_name");
```

- Add throws declaration for the exceptions in each method that uses log function, i.e.
```
public static void main(String[] args) throws IOException, ParseException, CryptoException  {
		
		ActionLogger.log("JAVA_APP,jar file test");		
	
	}
```