#!/bin/bash


# This script performs profiling on the target program and launches the clusterAdvisor.cpp program to generate reports
# Usage:
#	Script is called from icat main launch script. Can also be called directly.
#	bash cluster_mode.sh progName pathToProg progArgs
bash actionlogger.sh ICAT Cluster_advisor
prog=$1
DIR=$2
progargs=${@:3}

dir_icat=$(pwd)
#pwd
mkdir -p icat
cd icat
mkdir -p reports
mkdir -p logs

ICAT_PATH=$(pwd)

export ICAT_PATH=$(pwd)

echo "Determining the clustering mode..."

# isNUMA_aware -- program needs to be either nested omp or MPI+omp
# what type of parallel program

# Note from Ritu: just ask the user
### in this section, I am attempting to automatically determine if the code is OMP or MPI+OMP.
### However, I do not think this will work. Best bet is to just ask the user.
##
#src=${DIR}/${prog}.c*
#if [ -e "$(grep 'pragma.*omp' $src | xargs grep -L 'MPI_Comm_size' $src)"]
#then
#  isNUMA="$(echo "1")"
#  parallel="$(echo "omp")"
#elif [ -e "$(grep 'MPI_Comm_size' $src | grep 'pragma.*omp' $src)"]
#then
#  isNUMA="$(echo "1")"
#  parallel="$(echo "hybrid")"
#elif [ -e "$(grep 'MPI_Comm_size' $src | xargs grep -L 'pragma.*omp' $src)"]
#then
#  isNUMA="$(echo "0")"
#  parallel="$(echo "mpi")"
#else
# isNUMA="$(echo "0")"
# parallel="$(echo "other")"
#fi
###

echo "What is the programming model used in your application?"
echo "1. Nested OpenMP"
echo "2. MPI"
echo "3. OpenMP+MPI"
echo "4. None of the above/serial or OpenMP without nesting"
read choice

#choice=2
#echo "hello $choice bye"

case $choice in
1) isNUMA="$(echo "1")"
   parallel="$(echo "omp")"
;;
2) isNUMA="$(echo "0")"
   parallel="$(echo "mpi")"
;;
3) isNUMA="$(echo "1")"
   parallel="$(echo "hybrid")"
;;
4) isNUMA="$(echo "0")"
   parallel="$(echo "other")" 
;;
esac


# check for memAdvisor report to get memory allocation information. If file doesn't exist, prompt user for info.
filename=${prog}_memory_advisor_report.txt                
if [ -e "$ICAT_PATH/reports/$filename" ]
then
  mem="$(grep 'Memory.*Allocation:' $ICAT_PATH/reports/$filename | awk '{print $3}')"
else
  echo "ICAT cannot find file containing memory mode advise"
  #pwd
  #echo "reports/$filename"
  echo "Which memory allocation will your application use:"
  echo "1. Entire application allocated to DDR4"
  echo "2. Entire applcication allocated to HBM"
  echo "3. Selective allocation using memkind"
  read selection

  case $selection in
  1) mem="$(echo "DDR4")" ;;
  2) mem="$(echo "HBM")" ;;
  3) mem="$(echo "selective")";;
  esac
fi 

echo ${isNUMA} > $ICAT_PATH/logs/${prog}_cluster_data.txt
echo ${parallel} >> $ICAT_PATH/logs/${prog}_cluster_data.txt
echo ${mem} >> $ICAT_PATH/logs/${prog}_cluster_data.txt


#launch clusterAdvisor program to generate reports
#cd ..
#${dir_icat}clusterAdvisor $prog
#echo $PWD
$dir_icat/clusterAdvisor $prog
#clean up temp files
rm $ICAT_PATH/logs/${prog}_cluster_data.txt

#possibly add command that will append clustering report to memory advisor report, if it exists
#mv logs/${prog}_clustering_advisor_report.txt reports/${prog}_clustering_advisor_report.txt

echo "Report generated."
echo "----------------------"
echo " "

