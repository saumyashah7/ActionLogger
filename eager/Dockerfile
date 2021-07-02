FROM ubuntu:18.04

# Set locales
RUN apt-get update && \
apt-get install -y locales && \
locale-gen en_GB.UTF-8
ENV LANG en_GB.UTF-8
ENV LC_CTYPE en_GB.UTF-8

# Fix sh
RUN rm /bin/sh && ln -s /bin/bash /bin/sh

# Install dependencies
RUN apt-get update && \
apt-get install -y git build-essential curl wget software-properties-common libcrypto++-dev libcrypto++-doc libcrypto++-utils libcurlpp-dev libcurl4-openssl-dev

# Install OpenJDK 12
RUN \
add-apt-repository -y ppa:openjdk-r/ppa && \
apt-get update && \
apt-get install -y openjdk-11-jdk wget unzip tar && \
rm -rf /var/lib/apt/lists/*

# Define commonly used JAVA_HOME variable
ENV JAVA_HOME /usr/lib/jvm/java-11-openjdk-amd64/
EXPOSE 8080

# Create directories
RUN mkdir -p /home/json/cpp && mkdir -p /home/json/java && mkdir -p /home/decryptedfiles
ADD cppfiles /usr/cppfiles
RUN g++ -o /usr/cppfiles/decrypt /usr/cppfiles/decrypter.cpp -lcryptopp -lcurl
ADD target/eager-0.0.1-SNAPSHOT.war app.war
CMD ["java","-jar","app.war"]
