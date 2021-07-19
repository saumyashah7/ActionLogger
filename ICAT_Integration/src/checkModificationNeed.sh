#!/bin/bash

#to run this script individually, run the command named "checkModificationNeed.sh complete-path-and-name-of-the-executable>" 
#echo "Please make sure that the  Memory Advisor report for $1 exists in the subdirectory named "reports" created by ICAT."
bash actionlogger.sh ICAT Code_adaptation_advisor
source ~/.icatrc

bold=$(tput bold)
normal=$(tput sgr0)

if [ $# -eq 0 ]
  then
    echo " "
    echo "No arguments supplied by default."
    echo "Please enter the name of the executable that you profiled with our Memory Mode Advisor (that is, if you have already run the Memory Mode Advisor)."
    read prog
else
  prog=$1
fi
if grep -Fxq "Source code modification" ./icat/reports/${prog}_memory_advisor_report.txt
then
     echo "Modify source code."
     echo "ICAT can transform your source code to allocate certain bandwidth critical memory objects on HBM."
     echo "If you would like to proceed with code transformation using ICAT, please press 1."
     read choice
     if [ $choice -eq 1 ]; then
       echo "Please enter your source file name:"
       read fileName2
       echo "Please enter the path to the directory containing the source file:"
       read DIR
       CCP="${fileName2##*.}"
       if [[ ( "$CCP" == "c" ) || ( "$CCP" == "cpp" ) || ( "$CCP" == "C" ) ]]; then
          ${dir_icat}scriptHBW2.sh $DIR/$fileName2
       else
          ${dir_icat}scriptHBW2_Fortran.sh $DIR/$fileName2
       fi
     fi
else
     echo "Either the source code modification is not needed or the Memory Advisor report for $prog does not exist in the subdirectory named "reports""
     echo "However, if you would like to test how our source code modification script works, press 2, else press 3."
     read choice
     if [ $choice -eq 2 ]; then
       echo "Please enter your source file name:"
       read fileName2
       echo "Please enter the path to the directory containing the source file:"
       read DIR
       CCP="${fileName2##*.}"
       if [[ ( "$CCP" == "c" ) || ( "$CCP" == "cpp" ) || ( "$CCP" == "C" ) ]]; then
          ${dir_icat}scriptHBW2.sh $DIR/$fileName2
       else
          ${dir_icat}scriptHBW2_Fortran.sh $DIR/$fileName2
       fi 
     fi

fi

echo " "
echo "${bold}Please note: If your code was modified by ICAT to take advanatage of MCDRAM, then plese compile it with the "-lmemkind" flag.${normal}"
echo "${bold}You can run the code in the queue that is configured with MCDRAM in Flat mode or in hybrid mode (e.g., Flat-Quadrant queue on Stampede).${normal}"
echo "${bold}You might want to make sure that Intel/17.x.x compiler (or a higher version) is loaded at the time of compiling and running the code.${normal}"
echo ""

#if grep -Fxq "Source code modification" ./reports/${prog}_memory_advisor_report.txt

#then
#    echo "Modify source code."
#    echo "ICAT can transform your source code to allocate certain bandwidth critical memory objects on HBM."
#    echo "If you would like to proceed with code transformation using ICAT, please press 1."
#    read choice
#    if [ $choice -eq 1 ]; then
#      echo "Pls enter your source filename:"
#      read fileName2
#      ./scriptHBW2.sh $fileName2
#    fi
#else
#    echo "Either the source code modification is not needed or the report from the Memory Advisor does not exist in the subdirectory named "reports""
#    echo "However, if you would like to test how our source code modification script works, press 2, else press 3"
#    read choice
#    if [ $choice -eq 2 ]; then
#      echo "Pls enter your source filename:"
#      read fileName2
#      ./scriptHBW2.sh $fileName2
#    fi

#fi
