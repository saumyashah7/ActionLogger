# ActionLogger - Shell script

This shell script is developed to handle logging activities for the bash scripts or the applications that supports bash script. It logs the activities in any project supporting bash execution by accepting just two parameters. For usage please follow the steps listed below:

- Clone the repository
```
git clone https://github.com/saumyashah7/ActionLogger.git
```

- Copy the actionlogger script to the directory of each file which will be logging the activities or try using relative path to the scipt to avoid copying
```
cp ActionLogger/Bash/actionlogger.sh $DESTINATION
```

- Add execute permissions to the script
```
cd $DESTINATION
chmod +x actionlogger.sh
```

- Define the log with following parameters while calling the script
	- application_name(required) -> name of the application
	- metric_name(optional) -> name of the metric
```
bash actionlogger.sh application_nam metric_name
```

- Sample usage in a shell script
```
#! /bin/bash
a=1
b=1
function add(){
	bash actionlogger.sh addition two_numbers # addition->application_name, two_numbers->metric_name 
	echo $(($a+$b))
}
c=$(add)
echo "$c"
```
