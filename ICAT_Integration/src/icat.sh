#! /bin/bash

#****************************************************************
#***  Updated script to launch the ICAT tool
#***  Version of the script: 2.0
#***  Lars  Wed Apr 19 16:19:14 CDT 2017
#****************************************************************


#set -e
#set -x




#mkdir -p icat
#cd icat
#mkdir -p reports
#mkdir -p logs

bold=$(tput bold)
normal=$(tput sgr0)
b="\033[1m"
u="\033[4m"
n="\033[0m"

echo -e $n
echo "---------------------------------------------------------------------"
echo "-------- Welcome to ICAT :: Interactive Code Adaptation Tool --------"
echo "---------------------------------------------------------------------"
echo

bash actionlogger.sh ICAT

#***  Checking for global icatrc file
brc=false
frc=~/.icatrc
if [ -e ~/.icatrc ]; then
  brc=true
fi
#***  Checking for global icatrc file
if [ $brc = false ]; then
  frc=./.icatrc
  if [ -e ./.icatrc ]; then
    brc=true
  fi
fi
#***  Source icatrc file
if [ $brc = true ]; then
  source $frc
else
  echo Abort!
fi

echo "hello"
pwd
echo ${dir_icat}
echo "bye"

help=false
dryrun=false
while getopts "Hvhd" opt; do
  case $opt in
    H ) help=true          ;;
    v ) iverb=$((iverb+1)) ;;
    h ) ihint=$((ihint+1)) ;;
    d ) dryrun=true        ;;
  esac
done
shift $(($OPTIND - 1))

if [ $help = true ]; then
  echo Options for ICAT
  echo ----------------
  echo -e "\t-H\t\t: Help page (this output)"
  echo -e "\t-v\t\t: verbose output"
  echo -e "\t-h\t\t: hints enabled"
  echo -e "  \t\t\t  Hints are particularly useful when using the tool for the first time"
  echo
  exit
fi

if [ $iverb -ge 1 ]; then
  echo -e "\tVerbose output"
  echo -e "\tResource file  \t:: \t"$frc
  echo -e "\tMain directory \t:: \t"$dir_icat
  echo -e "\tVerbosity      \t:: \tlevel="$iverb
  echo -e "\tHints          \t:: \tlevel="$ihint
#  echo -e "\t\t:: \t"$
  echo
  echo
fi


#***  Acknowledge the use of compiler option '-h'
echo -e ${u}Step 1$n
echo -e ${b}"Purpose \t:${n} Acknowledge usage of compiler option '-h'"
if [ $ihint -ge 1 ]; then
echo -e ${b}"Hint    \t:${n} The compiler option '-g' adds debug information to the executable"
echo -e               "\t\t  This information is used by ICAT"
fi
echo -e ${b}"Question\t:${n} Please acknowledge that you have compiled the code with the '-g' option"
echo
echo -en "\t\t  Answer with y/n (y is the default) :: "
read input
input=${input:=y}
echo -e  "\t\t  You have answered with             :: ${b}$input$n"
if [ $input != 'y' -a $input != 'Y' ]; then
  echo
  echo -e "Code compiled without the compiler option '-g' cannot be analyzed by ICAT"
  echo -e "Please recompile code and restart ICAT analysis"
  echo -e "${b}ICAT :: Abort!$n"
  echo
  exit 1
fi


#***  Enter name of the excutable, the path, and program options
echo
echo -e ${u}Step 2$n
echo -e ${b}"Purpose \t:${n} Provide the name of the executable, the path, and optionally the program arguments"
if [ $ihint -ge 1 ]; then
echo -e ${b}"Hint    \t:${n} ICAT need to know how to start the executable"
fi
echo -en ${b}"Question\t:${n} Name of the executable? \t\t\t"
read prog
echo -en ${b}"\t\t${n}  Path to the executable? You may use . (dot)\t"
read dir
dir=${dir:="."}
if [ $dir = '.' ]; then
  dir=$(pwd)
fi
echo -en ${b}"\t\t${n}  Command line arguments, separated by commas?\t"
read progargs
if [ ! -d $dir ]; then
  echo
  echo Directory $dir does not exist or is not a directory
  echo -e "${b}ICAT :: Abort!$n"
  echo
  exit 1
fi
if [ ! -e ${dir}/${prog} ]; then
  echo
  echo Executable ${dir}/${prog} does not exist or is not a directory
  echo -e "${b}ICAT :: Abort!$n"
  echo
  exit 1
fi


while :
do
#***  Select the advice topic
  echo
  echo
  echo -e ${u}Step 3$n
  echo -e ${b}"Purpose \t:${n} Select advice topic"
  if [ $ihint -ge 1 ]; then
  echo -e ${b}"Hint    \t:${n} ICAT can advise you on a variety of topics"
  echo -e               "\t\t  You may select one or all topics"
  fi
  echo -e ${b}"Question\t:${n} Please select from one of these options"
  echo
  echo -e ${n}"Option${n}\t\t: ${n}Advice$n\t\t ${n}Description${n}"
  echo -e "-----------------------------------------------------"

  echo -e ${b}"1$n\t\t: Memory mode\t\t Exploit memory hierarchies"
if [ $ihint -ge 1 ]; then
  echo -e "\t\t\t\t\t - Some architectures provide more than one type of memory"
  echo -e "\t\t\t\t\t - The different types may vary in bandwidth and latency"
  echo -e "\t\t\t\t\t - Use the 'memory mode' to achieve the best bandwidth and/or latency"
  echo
fi
  echo -e ${b}"2$n\t\t: Cluster mode\t\t Exploit clustering of cores"
if [ $ihint -ge 1 ]; then
  echo -e "\t\t\t\t\t - Some architectures can group cores into tiles"
  echo -e "\t\t\t\t\t - The tile configuration may affect memory bandwidth"
  echo -e "\t\t\t\t\t   and/or on-node (MPI) communication bandwidth and latency"
  echo -e "\t\t\t\t\t - Use the 'cluster mode' to determine the best tiling"
  echo
fi
  echo -e ${b}"3$n\t\t: Vectorization mode\t Enable vector instructions"
if [ $ihint -ge 1 ]; then
  echo -e "\t\t\t\t\t - Vectorization allows for the computation of several results per clock cycle"
  echo -e "\t\t\t\t\t - Sometimes the comipiler fails to identify vectorization opportunities"
  echo -e "\t\t\t\t\t - Use the 'vectorization mode' to help the compiler to use vector instructions"
  echo
fi
  echo -e ${b}"4$n\t\t: Code adaptation \t Assign individual arrays to different memory types"
if [ $ihint -ge 1 ]; then
  echo -e "\t\t\t\t\t - This is similar to the first (1) option"
  echo -e "\t\t\t\t\t - Select the target memory for individual arrays"
  echo -e "\t\t\t\t\t - The high-bandwith memory is limited in size"
  echo -e "\t\t\t\t\t - Fill the high-bandwidth memory with smaller arrays"
  echo -e "\t\t\t\t\t - Use 'code adaptation' to achieve the best bandwidth and/or latency"
  echo -e "\t\t\t\t\t   for individual arrays"
  echo
fi
  echo -e ${b}"5$n\t\t: Memory optimization \t Is this the AoS to SoA transformation?"
if [ $ihint -ge 1 ]; then
  echo -e "\t\t\t\t\t - Add a hint here?"
  echo
fi
  echo -e ${b}"6$n\t\t: All \t\t\t Get all available advice at once"
  echo
  echo -e ${b}"0$n\t\t: Quit ICAT"
  echo

  echo
  echo -en "\t\t  Answer with a number between 0 and 6 (0 is the default) \t:: "
  read option
  option=${option:=0}
  echo -e  "\t\t  You have selected option \t\t\t\t\t:: ${b}$option$n"
if [ $option -lt 0 -a $option -gt 6 ]; then
  echo
  echo -e "You have selected an invalid option"
  echo -e "${b}ICAT :: Abort!$n"
  echo
  exit 1
fi
echo
echo

#echo "Please choose from the following ICAT options: "
#echo "1. Run Memory Mode Advisor" #user only wants suggestions on what memory mode to use
#echo "2. Run Cluster Mode Advisor" #user only wants suggestions for clustering mode
#echo "3. Run Vectorization Advisor (TBD)" #user wants report on changes for automatic vectorization
#echo "4. Run Code Adaptation Advisor - for using the high-bandwidth memory selectively" # user wants to modify the source code to allocate certian memory objects on the HBM
#echo "5. Run Memory Optimization Advisor (TBD)"
#echo "6. All" #user wants advice for best memory & cluster modes, plus AV potential
#echo "7. Quit ICAT"
#read option

case $option in
   0)
      echo
      echo Exiting ICAT
      echo
      exit
      ;;
   1) 
#***  Launch Memory profiling script
      echo -e "\tOption 1: ${b}Memory advisor$n"
      echo -e "\t------------------------"
      if [ $dryrun = true ]; then
        echo
        echo Dry run :: bash -i ${dir_icat}mem_mode.sh $prog $dir $progargs 2> /dev/null
        echo
      else
        bash -i ${dir_icat}mem_mode.sh $prog $dir "$progargs" 2> /dev/null
      fi
      ;;
   2)
#***  Launch clustering profiling script
      echo -e "\tOption 2: ${b}Cluster advisor$n"
      echo -e "\t-------------------------"
      if [ $dryrun = true ]; then
        echo
        echo Dry run :: bash -i ${dir_icat}cluster_mode.sh $prog $dir $progargs 2> /dev/null
        echo
      else
        bash -i ${dir_icat}cluster_mode.sh $prog $dir $progargs 
      fi
      ;; 
   3) 
#***  Launch Automatic Vectorization profiling script
      echo -e "\tOption 3: ${b}Vectorization advisor$n"
      echo -e "\t-------------------------------"
      if [ $dryrun = true ]; then
        echo
        echo Dry run :: bash -i ${dir_icat}av_mode.sh $prog $dir $progargs
        echo
      else
        bash -i ${dir_icat}av_mode.sh $prog $dir $progargs
      fi
      ;; 
   4)
#***  Code adaptation
      echo -e "\tOption 4: ${b}Code adaptation advisor$n"
      echo -e "\t-------------------------------------"
      echo "We will need some input from you to determine if the code adaptation is needed or not."
      if [ $dryrun = true ]; then
        echo
        echo Dry run :: bash -i ${dir_icat}checkModificationNeed.sh $prog $dir  
        echo
      else
        bash -i ${dir_icat}checkModificationNeed.sh $prog $dir  
      fi
      ;;
   5)  
#***  Memory usage optimization
      echo -e "\tOption 5: ${b}Memory optimization advisor$n"
      echo -e "\t-----------------------------------------"
      if [ $dryrun = true ]; then
        echo
        echo Dry run :: bash -i ${dir_icat}memory_opt_mode.sh $prog $dir $progargs
        echo
      else
        bash -i ${dir_icat}memory_opt_mode.sh $prog $dir $progargs
      fi
      ;;
   6) 
#***  Launch profiling script for all advisors
      echo -e "\tOption 6: ${b}All advice available$n"
      echo -e "\t-----------------------------------------"
      if [ $dryrun = true ]; then
        echo
        echo Dry run :: bash -i ${dir_icat}all_mode.sh $prog $dir $progargs
        echo
      else
        source ${dir_icat}all_mode.sh $prog $dir $progargs
      fi
      ;; 
   *) echo "Invalid Option";;
esac
  echo
  echo -en "\t\t  Do you want to exit ICAT?"
  echo
  echo -en "\t\t  Answer with y/n (y is the default) :: "
  read option
  option=${option:=y}
  if [ $option = 'y' ]; then
    echo
    echo Exiting ICAT
    echo
    break
  fi
done

#rm memObj.txt
#clean up temp files
#rm ${prog}_data.txt
#rm ${prog}_condensed_report.txt
#rm vtune_${prog}.txt

