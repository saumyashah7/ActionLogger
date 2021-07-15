#! /bin/bash

function getToken(){
	echo $(curl -s $url/getToken)
}

function log(){
	token=$(getToken)
	if [ -z "$metric" ] 
	then
		curl -s $url/log/$token/$app
	else
		curl -s $url/log/$token/$app/$metric
	fi
}
if [ "$#" -lt 1 ] 
then
	echo "Provide at least one argument"
	exit
fi

if [ "$#" -gt 2 ] 
then
	echo "Can only provide application and metric name"
	exit
fi

url=url_of_application
app=$1
metric=$2

log
