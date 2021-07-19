#! /bin/bash

# Script for adapting Fortran code by allocating specific arrays to the HBM - use " !DIR$ ATTRIBUTE FASTMEM" for arrays to be put in HBM
# It should be noted that the array that is decorate with the aformentioned attribute, should be an allocatable array. 
# What canNOT be decorated with the aforementioned attribute: Local variable, a common block, or an array that will be allocated on the stack.

# To use this script: "./scriptHBW2_Fortran name_of_the_program_file"

# Make sure that your paltform supports HBM to avoid any run-time errors. 
#You can determine if the HBM is available and if the libraries required for HBM support are linked into the executable by calling the FOR_GET_HBW_AVAILABILITY() function. 
#You can change the behavior of the ALLOCATE statement when HBM is not available or if the required libraries are not linked into the executable by calling the FOR_SET_FASTMEM_POLICY() function. 
#Alternatively, you can set environment variables FOR_FASTMEM_RETRY or FOR_FASTMEM_RETRY_WARN.

# When you use this directive in a program, you must specify the following on the compiler or linker command line: -lmemkind

# this script needs to be updated so that the insertion of directives happens only once for each array
#echo " "
echo " "
bold=$(tput bold)
normal=$(tput sgr0)

#set -e
#set -x

list1=($(grep -nw "allocatable" $1| grep "::"|cut -d: -f1))

#for index in ${!list1[*]}
#do
#    printf "%4d: %s\n" $index ${list1[$index]}
#done


length=${#list1[@]}
for ((i = 0; i != length; i++)); do
   echo "${bold}Line # '${list1[i]}' of your program contains array/s that can be allocated on MCDRAM.${normal}"
   read -p "Would you like to allocate all or some of the array/s from MCDRAM? (type "y" for yes, and "n" for no)" response;
   if [[ "$response" == "y" ]]
   then
             #echo "hello"
             #head -${list1[i]} $1 | tail -1 | grep "allocatable"|sed -n -e 's/^.*:: //p'
             #grep -nw "allocatable" |sed -n -e 's/^.*:: //p'
             lineNum="$((${list1[i]}+$i))"
             #"$(($num1+$num2))"
             #echo $lineNum
             arrayName=($(head -${lineNum} $1 | tail -1 | grep "allocatable"|sed -n -e 's/^.*:: //p')) 
             echo "The arrays included in the declaration containing the allocatable attribute will be printed one by one."
             for index in ${!arrayName[*]}
	     do
    		printf "%4d: %s\n" $index ${arrayName[$index]}|tr -d ,
                read -p "Would you like to allocate the aforementioned array on MCDRAM?(type "y" for yes, and "n" for no)" response2;
                if [[ "$response2" == "y" ]]
                then
                   tmpName=${arrayName[$index]}
                   tmpName=$(echo $tmpName | sed s/,//g)
                   tmpName=$(echo $tmpName | cut -f1 -d"(")
                   if grep -q "\!DIR\$ ATTRIBUTES FASTMEM :: ${tmpName}" $1; then
		      echo "A directive for allocating this array on FASTMEM already exists in this file. "
                      read -p "Should ICAT insert an additional directive?(type "y" for yes, and "n" for no)" response3;
                      if [[ "$response3" == "y" ]]
                      then
                          sed -i "${lineNum} a \!DIR\$ ATTRIBUTES FASTMEM :: ${tmpName}" $1    
                      else
                          sed -i "${lineNum} a \  " $1
                      fi
                   else
		      sed -i "${lineNum} a \!DIR\$ ATTRIBUTES FASTMEM :: ${tmpName}" $1 
                   fi
                
                fi

             done
   fi
done

list1=($(grep -nw "ALLOCATABLE" $1| grep "="|cut -d: -f1))
#echo  $list1
length=${#list1[@]}
for ((i = 0; i != length; i++)); do
   echo "${bold}Line # '${list1[i]}' of your program contains array/s that can be allocated on MCDRAM.${normal}"
   read -p "Would you like to allocate all or some of the array/s from MCDRAM? (type "y" for yes, and "n" for no)" response;
   if [[ "$response" == "y" ]]
   then
             #echo "hello"
             #head -${list1[i]} $1 | tail -1 | grep "ALLOCATABLE"|sed -n -e 's/^.*:: //p'
             #grep -nw "ALLOCATABLE" |sed -n -e 's/^.*:: //p'
             lineNum="$((${list1[i]}+$i))"
             #"$(($num1+$num2))"
             echo $lineNum
             arrayName=($(head -${lineNum} $1 | tail -1 | grep "ALLOCATABLE"|sed -n -e 's/^.*:: //p'))
             echo "The arrays included in the declaration containing the ALLOCATABLE attribute will be printed one by one."
             for index in ${!arrayName[*]}
             do
                printf "%4d: %s\n" $index ${arrayName[$index]}|tr -d ,
                read -p "Would you like to allocate the aforementioned array on MCDRAM?(type "y" for yes, and "n" for no)" response2;
                if [[ "$response2" == "y" ]]
                then
                   tmpName=${arrayName[$index]}
                   tmpName=$(echo $tmpName | sed s/,//g)
                   tmpName=$(echo $tmpName | cut -f1 -d"(")
                   sed -i "${lineNum} a \!DIR\$ ATTRIBUTES FASTMEM :: ${tmpName}" $1
                fi

             done
   fi
done

echo " "
echo "${bold}Program modification is complete. The modified program is presented below.${normal}"
echo " "
#cat $1
grep -E --color '\!DIR\$ ATTRIBUTES FASTMEM|$' $1
