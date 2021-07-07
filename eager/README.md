# ActionLogger - Docker

This is an installation guide for Actionlogger web application using docker. This version only takes http into consideration. The version with https will be released soon.


## Instructions

Make sure [Docker](https://docs.docker.com/get-docker/) and [Docker-compose](https://docs.docker.com/compose/install/) is installed on the machine.

### Webapplication setup

- Become a root user and install maven
```
sudo su -
apt install maven -y
```

- Go to the local directory where the project repository can be copied and clone the github repository for the project
```
git clone https://github.com/saumyashah7/ActionLogger.git
```

- Go to the Eager directory
```
cd ActionLogger/Eager
```

- Edit the values of environment variables in docker-compose and relatively in apllication.properties files as per your choice. If you'd like to use default credentials just skip this step
```
vi docker-compose.yml
vi src/main/resources/application.properties
```
- Build the war files
```
mvn clean package
```

- Bring up the containers
```
docker-compose up -d --build
```

- Access the application with following URL
```
http://URL_OF_MACHINE:8080/
```