# ActionLogger - ICAT Integration

This document will walk through the process to integrate actionlogger in an application using shell script. ICAT is a great candidte to demostrate that capability. The application can be tracked with just a call to the [shell script](https://github.com/saumyashah7/ActionLogger/blob/master/Bash/actionlogger.sh) with one required and one optional parameter as follows:

- application_name(required) -> name of the application
- metric_name(optional) -> name of the metric
```
bash actionlogger.sh application_nam metric_name
```

Make sure the URL of eager webapplication is updated inside [action logger script](./src/actionlogger.sh)

To track only application usage refer to the function call in [main script](./src/icat.sh):
```
bash actionlogger.sh ICAT
```

To track individual submodules, refer to function calls in individual src directory shell scripts:
```
bash actionlogger.sh ICAT All_advisor
bash actionlogger.sh ICAT Code_adaptation_advisor
bash actionlogger.sh ICAT Vectorization_advisor
bash actionlogger.sh ICAT Cluster_advisor
bash actionlogger.sh ICAT Memory_advisor
bash actionlogger.sh ICAT Memory_optimization_advisor
```