FROM ubuntu:18.04



MAINTAINER Carlos Moro <cmoro@deusto.es>



ENV TOMCAT_VERSION 8.5.50



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



# Get Tomcat

RUN wget --quiet --no-cookies https://archive.apache.org/dist/tomcat/tomcat-8/v${TOMCAT_VERSION}/bin/apache-tomcat-${TOMCAT_VERSION}.tar.gz -O /tmp/tomcat.tgz && \

tar xzvf /tmp/tomcat.tgz -C /usr && \

mv /usr/apache-tomcat-${TOMCAT_VERSION} /usr/tomcat && \

rm /tmp/tomcat.tgz && \

rm -rf /usr/tomcat/webapps/examples && \

rm -rf /usr/tomcat/webapps/docs && \

rm -rf /usr/tomcat/webapps/ROOT



# Add admin/admin user

ADD tomcat-users.xml /usr/tomcat/conf/



ENV CATALINA_HOME /usr/tomcat

ENV PATH $PATH:$CATALINA_HOME/bin



EXPOSE 8080

EXPOSE 8009

VOLUME "/usr/tomcat/webapps"

WORKDIR /usr/tomcat


# Launch Tomcat

CMD ["/usr/tomcat/bin/catalina.sh", "run"]
