#!/bin/bash

chown -R mysql:mysql /var/lib/mysql 

# Starts MySQL
/etc/init.d/mysql start
sleep 10
chown -R mysql:mysql /var/run/mysqld


mysql -u root -e "create database $mysql_database"; # create a mysql database
mysql -u root -e "USE mysql; UPDATE user SET plugin='mysql_native_password' WHERE User='root'; FLUSH PRIVILEGES;"

mysqladmin -u root password "$mysql_password"

cd Bug_Collection # going to Bug_Collection folder
python3 Data_Populate.py -host localhost -database "$mysql_database" -user root -passwd "$mysql_password" # populating the database
cd .. # get back to the original folder


# To use the pre-compiled jar file
#java -jar BugDatabase.jar "$mysql_database" root "$mysql_password"

# Intructions for compilations from source

export CLASSPATH=/IPT/UI/mysql-connector-java-8.0.16.jar:/IPT/UI/json-20180813.jar:$CLASSPATH
echo "display: "$DISPLAY
javac -cp mysql-connector-java-8.0.16.jar -cp json-20180813.jar BaseView_v4.java HighlightTreeCellRenderer.java
java BaseView_v4 "$mysql_database" root "$mysql_password"

