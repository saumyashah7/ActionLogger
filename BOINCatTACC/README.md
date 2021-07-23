# ActionLogger - BOINC@TACC Integration

- This is a guide to assist in integrating web based applications with action logger to track the activities. BOINC@TACC provides a conduit for routing High-Throughput Computing (HTC) jobs from TACC resources to a BOINC server, and from there, to the BOINC clients (which run on volunteered hardware resources and VMs in the cloud). BOINC@TACC demonstartes how easy it can be to integrate Actionlogger in your web application. 

- Only one file is changed in the actual BOINC@TACC code to integrate the Actionlogger which is [util.inc](./inc/util.inc) file. A function **actionlog** is added and called inside the **page_head** function and that's about it. Similarly, it can be integrated in any web application by integrating such function in header/footer file which simply gets the token to add the activity and logs the activity via post request.

- The installation can be completed as mentioned in [README.md](./docs/README.md) file.