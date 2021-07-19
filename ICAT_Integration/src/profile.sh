#!/bin/bash


prog=$1
DIR=$2
USER=`whoami`
myProc=""

touch ./logs/mem.tmp

until [ -n "$myProc" ]; do #repeat profiling until program has executed
  rm ./logs/mem.tmp
  trap 'exit' SIGINT SIGTERM 15
  for i in `ps -u $USER | grep -v grep | grep -v bash | grep -v profile | grep -v sshd |grep -v slurm| grep -v perf| grep -v sleep | grep -v timeout | awk 'NR > 1 {print $1}'`; do pmap -x $i 2> /dev/null 1>> ./logs/mem.tmp; done
  myProc="$(grep "$prog" ./logs/mem.tmp | grep -v grep | grep -v bash)"
done

mem="$(grep total ./logs/mem.tmp | awk '{sum += $3} END { print sum}')"

printf "%d \n" $mem > ./logs/${prog}_data.txt

