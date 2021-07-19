#!/bin/bash

#set -e
#set -x

# This script performs profiling on the target program and launches the memAdvisor.cpp program to generate reports
# Usage:
#	Script is called from icat main launch script. Can also be called directly.
#	bash mem_mode.sh progName pathToProg progArgs
# if calling directly, then please read the comment below
bash actionlogger.sh ICAT Memory_advisor
prog=$1
DIR=$2
progargs=${@:3}

source ~/.icatrc
#dir_icat=/work/01698/rauta/stampede2/testICAT/

# Uncomment the following lines if calling this script directly
mkdir -p icat
cd icat
mkdir -p reports
mkdir -p logs


ICAT_PATH=$(pwd)

export ICAT_PATH=$(pwd)

#echo "the current path in mem_mode.sh is: "
#pwd

#echo "hello"
#echo ${dir_icat}
#echo "bye"

echo "Does your code use MPI programming model? (Enter 1 or 2.)"
echo "1. Yes"
echo "2. No"
read choice1

case $choice1 in
 1) prog_type="$(echo "mpi")"
 ;;
 2) prog_type="$(echo "other")"
 ;;
esac

echo "You chose $choice1"

if [ "$prog_type" = "other" ]
then
# call the script mem_mode_other.sh
source ${dir_icat}mem_mode_other.sh $prog $DIR "$progargs" 
else
# call the script mem_mode_mpi.sh
source ${dir_icat}mem_mode_mpi.sh $prog $DIR "$progargs"
fi

echo "Report generated."
echo "----------------------"
echo " "
