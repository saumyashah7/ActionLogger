# ActionLogger - C++

This library is developed to handle client side operations for C++ applications. It comes in handy for desktop applications since the web applications can log activities via GET and POST requests. To log the activities in any C++ project please follow the steps listed below:

- Clone the repository
```
git clone https://github.com/saumyashah7/ActionLogger.git
```

- Copy the eagercpp directory to the directory of each C++ code file which will be logging the activities
```
cp -r ActionLogger/CPP/eagercpp $DESTINATION
```

- Import the action logger header file in C++ file as follows
```
#include "eagercpp/actionlogger.hpp"
```

- Define the log with following parameters
	- application_name(required) -> name of the application
	- metric_name(optional) -> name of the metric
```
log("application_name,metric_name");
```

- Sample usage in a C++ application
```
#include <iostream>
#include "eagercpp/actionlogger.hpp"

int main()
{
        log("testprojectcpp,some description");
        log("testprojectcpp");
		//Do something

        return 0;
}
```