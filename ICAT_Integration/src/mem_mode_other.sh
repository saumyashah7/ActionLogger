#!/bin/bash

# this script will be updated to avoid running the program twice - use time command like the MPI version if the script uses
#set -e
#set -x

prog=$1
DIR=$2
progargs=${@:3}

ICAT_PATH=$(pwd)

#echo "the current path in mem_mode_other.sh is: "
#pwd

echo "Profiling program..."

START=$(date +%s.%N)
#collect performance counter values if the program is not an MPI one - need to check if this works correctly for MPI programs especially with master-worker pattern
echo "Running perf command ..."
#perf stat -x '\t' -o ./logs/perfStat.tmp -e instructions,L1-dcache-loads,L1-dcache-stores,LLC-loads,LLC-stores,L1-dcache-load-misses,L1-dcache-store-misses,LLC-load-misses,LLC-store-misses $DIR/$prog $progargs 2>&1 > /dev/null

perf stat -x '\t' -o ./logs/perfStat.tmp -e instructions,L1-dcache-loads,L1-dcache-stores,LLC-loads,LLC-stores,L1-dcache-load-misses,L1-dcache-store-misses,LLC-load-misses,LLC-store-misses $DIR/$prog "$progargs"

END=$(date +%s.%N --date '5 seconds')
timeToWait=$(echo "scale=0;($END - $START + 0.5)/1" | bc)

#execute profiling script in BG; timeout after runtime+5 secs if PID of prog is not found
#timeout $timeToWait bash /home1/01698/rauta/icat/src/src/profile.sh $prog $DIR &
#timeout $timeToWait /home1/01698/rauta/icat/src/src/profile.sh $prog $DIR &

timeout $timeToWait ${dir_icat}profile.sh $prog $DIR &
pid=$!

sleep 1
# start timer and execute program to be profiled.
START=$(date +%s.%N)

echo "Running the program again..."
echo "${@:3}"
echo "RITU RITU"
#$DIR/$prog $progargs 2>&1 > /dev/null
$DIR/$prog "${@:3}" 

END=$(date +%s.%N)

runtime=$(echo "$END - $START" | bc)

sleep 5

testTrap()
{
 echo "runtime_belowMin or process is being backgrounded or background process trying to read."
 ps ajfx| grep rauta| grep -v grep
 mainProcId=`pgrep -f all_mode.sh|head -1`
 echo $mainProcId > readProcId.txt
 #pgrep -f "all_mode.sh"|  xargs kill -SIGCONT
 #kill -SIGCONT $testid
 #ps ajfx| grep rauta| grep -v grep
 #exit
 trap - SIGTTIN 
}
trap 'testTrap' SIGTTIN SIGINT SIGTERM
#trap runtime_belowMin SIGINT SIGTERM 15
wait $pid
if [ $? -ne 0 ]
then
    echo "ERROR: Application runtime ${runtime} too short to profile."
    echo "Exiting profiler and returning to ICAT main menu..."
    echo " "
    exit
fi

#launch vtune to find partitionable data if the memory usage is >= 16GB
#the file $prog_data.txt was generated by running profile.sh
mem="$(head -n 1 ./logs/${prog}_data.txt)"

if [ "$mem" -ge 16777216 ]; then #16777216 KB == 16 GB; use smaller tmp num for testing purposes- e.g., 16 in place of 16777216
 
  ml vtune
  #the following path would need to be updated with an upgrade in Vtune version
  #source /opt/intel/vtune_amplifier_xe_2016.4.0.470476/amplxe-vars.sh
  #source /opt/apps/intel/13/vtune_amplifier_xe_2013/amplxe-vars.sh
  #source /opt/intel/vtune_amplifier_xe_2017.3.0.510739/amplxe-vars.sh
  source /opt/intel/vtune_amplifier_xe_2017.4.0.518798/amplxe-vars.sh 
  amplxe-cl -c memory-access -knob analyze-mem-objects=true -r ${prog}_vtune $DIR/$prog , "$progargs" > ./logs/vtune_${prog}.txt
  amplxe-cl -report hotspots -group-by memory-object -S="L2 Miss Count" -r ${prog}_vtune > ./logs/${prog}_condensed_report.txt
 
  
  awk '!/Unknown/' ./logs/${prog}_condensed_report.txt | awk '!/Stack/' | awk '!/Memory/' | awk '!/--------/' | awk '{print $1 $2}' | sed -r 's/[:(]+/ /g' > ./logs/mObj.tmp
  awk '!seen[$0]++' ./logs/mObj.tmp > ./logs/memObj.tmp

  if [ ! -s "./logs/memObj.tmp" ]; then
    BWCfound="0" #file is empty
  else
    BWCfound="1" #file is not empty
  fi

  rm ./logs/mObj.tmp

else
  BWCfound="0"
fi

#process files: remove unneeded columns and lines from files
awk -F "[\t]" '{$2=""}1' ./logs/perfStat.tmp >> ./logs/perf.tmp
sed '1,2d' ./logs/perf.tmp >> ./logs/${prog}_data.txt
echo $runtime >> ./logs/${prog}_data.txt
echo $BWCfound >> ./logs/${prog}_data.txt

filename="memObj.txt"
if [ $BWCfound -ne 0 ]; then #if partitionable data found, include the srcfile and line number of the mem objects
  awk '{print $1,$2}' ./logs/memObj.tmp >> ./logs/memObj.txt
#fi
  #filename="memObj.txt"
  while IFS='' read -r line 
  do
    echo $line >> ./logs/srcSnippet.txt
    srcfile="$(echo $line | awk '{print $1}')"
    linenum="$(echo $line | awk '{print $2}')"
    sed -n "${linenum}p" ${DIR}/${srcfile} >> ./logs/srcSnippet.txt
    echo " " >> ./logs/srcSnippet.txt 
  done < ./logs/$filename

  mv ./logs/srcSnippet.txt ./logs/${prog}_memObj.txt
  rm ./logs/memObj.txt
fi

cd ..
#launch memAdvisor program to generate reports
${dir_icat}memAdvisor $prog
cd icat
#clean up temp files
rm ./logs/perfStat.tmp
rm ./logs/perf.tmp
rm ./logs/mem.tmp
rm ./logs/memObj.tmp
rm -rf ${prog}_vtune

#pwd
#clean up temp files if needed
#rm ./logs/${prog}_data.txt
#rm ./logs/${prog}_condensed_report.txt
#rm ./logs/vtune_${prog}.txt

