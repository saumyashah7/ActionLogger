#invoke this script as follows:  bash -i /scratch/01698/rauta/icat/src/all_mode.sh circuit_mpi /scratch/01698/rauta/icat/  2> /dev/null 
#!/bin/bash

#set -x
#set -e

#mkdir -p icat
#cd icat
#mkdir -p reports
#mkdir -p logs

#source /scratch/01698/rauta/icat/src/mem_mode.sh $1 $2 $3 
#echo "the current path in all_mode.sh is: "
#pwd
bash actionlogger.sh ICAT All_advisor
bash -i ${dir_icat}mem_mode.sh $1 $2 $3 2> /dev/null
bash -i ${dir_icat}cluster_mode.sh $1 $2 $3
bash -i ${dir_icat}av_mode.sh $1 $2 $3
bash -i ${dir_icat}memory_opt_mode.sh $1 $2 $3
bash -i ${dir_icat}checkModificationNeed.sh $1 
