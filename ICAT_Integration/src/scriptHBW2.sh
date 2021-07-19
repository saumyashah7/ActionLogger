#! /bin/bash

# Script for searching and replacing malloc/calloc/realloc/free calls with the respective HBW interface calls
# To use this script: "./scriptHBW name_of_the_program_file"

#echo " "
echo " "
bold=$(tput bold)
normal=$(tput sgr0)

#set -e
#set -x

insertDone=0

list1=($(grep -nw "malloc" $1| grep "="|cut -d: -f1))
#echo  $list1
length=${#list1[@]}


for ((i = 0; i != length; i++)); do
   echo "${bold}Line # '${list1[i]}' of your program contains a call to malloc that can be replaced with a call to hbw_malloc to take advanatage of MCDRAM.${normal}"
   read -p "Do you want to replace the calls to malloc?(type "y" for yes, and "n" for no)" response;
   if [[ "$response" == "y" ]]
   then
           bufToFree=`sed "${list1[i]}!d" $1| cut -d= -f1`
           originalLine="$(grep "malloc" $1 | grep "="| grep "$bufToFree")"
           #echo "${originalLine}"
           updatedLine="$(echo "$originalLine")"
           #echo "RITU"
           #echo "${updatedLine}"
           #echo "BYE"
           #updatedLine2="${updatedLine/malloc/hbw_malloc}"
           updatedLine2="$(echo "$updatedLine"| sed 's/malloc/hbw_malloc/g' )"
	   #echo "${updatedLine2}"
           
           escaped_lhs=$(printf '%s\n' "$originalLine" | sed 's:[][\/.^$*]:\\&:g')
           escaped_rhs=$(printf '%s\n' " if (checkHBMAvailability == 0){ $updatedLine2 } else{ $originalLine }" | sed 's:[\/&]:\\&:g;$!s/$/\\/')
           

           sed -i.bak "s/$escaped_lhs/$escaped_rhs/" $1

           #sed -i.bak "${list1[i]}s/malloc/hbw_malloc/" $1

           #sed -i.bak "${list1[i]}i\\ if (checkHBMAvailability == 0){ $updatedLine2 } else{ $originalLine } \\" $1
           #sed -i.bak "${list1[i]}i\\ //call to malloc is replaced with call to hbw_malloc if the HBM is available.\\" $1

           testr="$(echo $bufToFree| xargs)"
           #echo $testr
           list2="$(grep -nE "free.*$testr" $1|cut -d: -f1)"
           #echo $list2
	   length2=${#list2[@]}
           originalLineFree="$(grep -nE "free.*$testr" $1| cut -d: -f2)"
           #echo "line to free:"
           #echo "${bufToFree}"
           #echo "${originalLineFree}"
           updatedLineFree="$(echo $originalLineFree)"
           #echo "${updatedLineFree}"
           updatedLine2Free="${updatedLineFree/free/hbw_free}"
           #echo "line to free:"
           #echo  "${updatedLine2Free}"

           escaped_lhs2=$(printf '%s\n' "$originalLineFree" | sed 's:[][\/.^$*]:\\&:g')
           escaped_rhs2=$(printf '%s\n' " if (checkHBMAvailability == 0){ $updatedLine2Free } else{ $originalLineFree }" | sed 's:[\/&]:\\&:g;$!s/$/\\/')

           for((j=0; j != length2; j++)); do
               echo " "
               echo " "
	       echo "${bold}Lines with call to the function free that is paired with the recently replaced malloc call, should be replaced with hbw_free.${normal}"
               echo "Therefore, ICAT will replace the call to function free, with hbw_free if it is found in the program file."
	       #sed -i.bak "${list2[j]}s/free/hbw_free/" $1
               sed -i.bak "s/$escaped_lhs2/$escaped_rhs2/" $1
               echo " "
               echo " "
	   done
           insertDone=$((insertDone+1))
   fi
done

list1=($(grep -nw "calloc" $1| grep "="|cut -d: -f1))
#echo  $list1
length=${#list1[@]}
for ((i = 0; i != length; i++)); do
   echo "${bold}Line # '${list1[i]}' of your program contains a call to calloc that can be replaced with a call to hbw_calloc to take advanatage of MCDRAM.${normal}"
   read -p "Do you want to replace the calls to calloc?(type "y" for yes, and "n" for no)" response;
   if [[ "$response" == "y" ]]
   then
           bufToFree=`sed "${list1[i]}!d" $1| cut -d= -f1`
           originalLine="$(grep "calloc" $1 | grep "="| grep "$bufToFree")"
           #echo "${originalLine}"
           updatedLine="$(echo "$originalLine")"
           #echo "${updatedLine}"
           #updatedLine2="${updatedLine/calloc/hbw_calloc}"
           updatedLine2="$(echo "$updatedLine"| sed 's/calloc/hbw_calloc/g' )"
           #echo "${updatedLine2}"

           escaped_lhs=$(printf '%s\n' "$originalLine" | sed 's:[][\/.^$*]:\\&:g')
           escaped_rhs=$(printf '%s\n' " if (checkHBMAvailability == 0){ $updatedLine2 } else{ $originalLine }" | sed 's:[\/&]:\\&:g;$!s/$/\\/')


           sed -i.bak "s/$escaped_lhs/$escaped_rhs/" $1

           #sed -i.bak "${list1[i]}s/calloc/hbw_calloc/" $1

           #sed -i.bak "${list1[i]}i\\ if (checkHBMAvailability == 0){ $updatedLine2 } else{ $originalLine } \\" $1
           #sed -i.bak "${list1[i]}i\\ //call to calloc is replaced with call to hbw_calloc if the HBM is available.\\" $1

           testr="$(echo $bufToFree| xargs)"
           #echo $testr
           list2="$(grep -nE "free.*$testr" $1|cut -d: -f1)"
           #echo $list2
           length2=${#list2[@]}
           originalLineFree="$(grep -nE "free.*$testr" $1| cut -d: -f2)"
           #echo "line to free:"
           #echo "${bufToFree}"
           #echo "${originalLineFree}"
           updatedLineFree="$(echo $originalLineFree)"
           #echo "${updatedLineFree}"
           updatedLine2Free="${updatedLineFree/free/hbw_free}"
           #echo "line to free:"
           #echo  "${updatedLine2Free}"

           escaped_lhs2=$(printf '%s\n' "$originalLineFree" | sed 's:[][\/.^$*]:\\&:g')
           escaped_rhs2=$(printf '%s\n' " if (checkHBMAvailability == 0){ $updatedLine2Free } else{ $originalLineFree }" | sed 's:[\/&]:\\&:g;$!s/$/\\/')

           for((j=0; j != length2; j++)); do
               echo " "
               echo " "
               echo "${bold}Lines with call to the function free that is paired with the recently replaced calloc call, should be replaced with hbw_free.${normal}"
               echo "Therefore, ICAT will replace the call to function free, with hbw_free if it is found in the program file."
               #sed -i.bak "${list2[j]}s/free/hbw_free/" $1
               sed -i.bak "s/$escaped_lhs2/$escaped_rhs2/" $1
               echo " "
               echo " "
           done
           insertDone=$((insertDone+1))
   fi
done


list1=($(grep -nw "realloc" $1| grep "="|cut -d: -f1))
#echo  $list1
length=${#list1[@]}
for ((i = 0; i != length; i++)); do
   echo "${bold}Line # '${list1[i]}' of your program contains a call to realloc that can be replaced with a call to hbw_realloc to take advanatage of MCDRAM.${normal}"
   read -p "Do you want to replace the calls to realloc?(type "y" for yes, and "n" for no)" response;
   if [[ "$response" == "y" ]]
   then
           bufToFree=`sed "${list1[i]}!d" $1| cut -d= -f1`
           originalLine="$(grep "realloc" $1 | grep "="| grep "$bufToFree")"
           #echo "${originalLine}"
           updatedLine="$(echo $originalLine)"
           #echo "${updatedLine}"
           updatedLine2="$(echo "$updatedLine"| sed 's/realloc/hbw_realloc/g' )"
           #updatedLine2="${updatedLine/realloc/hbw_realloc}"
           #echo "${updatedLine2}"

           escaped_lhs=$(printf '%s\n' "$originalLine" | sed 's:[][\/.^$*]:\\&:g')
           escaped_rhs=$(printf '%s\n' " if (checkHBMAvailability == 0){ $updatedLine2 } else{ $originalLine }" | sed 's:[\/&]:\\&:g;$!s/$/\\/')


           sed -i.bak "s/$escaped_lhs/$escaped_rhs/" $1

           #sed -i.bak "${list1[i]}s/realloc/hbw_realloc/" $1

           #sed -i.bak "${list1[i]}i\\ if (checkHBMAvailability == 0){ $updatedLine2 } else{ $originalLine } \\" $1
           #sed -i.bak "${list1[i]}i\\ //call to realloc is replaced with call to hbw_realloc if the HBM is available.\\" $1

           testr="$(echo $bufToFree| xargs)"
           #echo $testr
           list2="$(grep -nE "free.*$testr" $1|cut -d: -f1)"
           #echo $list2
           length2=${#list2[@]}
           originalLineFree="$(grep -nE "free.*$testr" $1| cut -d: -f2)"
           #echo "line to free:"
           #echo "${bufToFree}"
           #echo "${originalLineFree}"
           updatedLineFree="$(echo $originalLineFree)"
           #echo "${updatedLineFree}"
           updatedLine2Free="${updatedLineFree/free/hbw_free}"
           #echo "line to free:"
           #echo  "${updatedLine2Free}"

           escaped_lhs2=$(printf '%s\n' "$originalLineFree" | sed 's:[][\/.^$*]:\\&:g')
           escaped_rhs2=$(printf '%s\n' " if (checkHBMAvailability == 0){ $updatedLine2Free } else{ $originalLineFree }" | sed 's:[\/&]:\\&:g;$!s/$/\\/')

           for((j=0; j != length2; j++)); do
               echo " "
               echo " "
               echo "${bold}Lines with call to the function free that is paired with the recently replaced realloc call, should be replaced with hbw_free.${normal}"
               echo "Therefore, ICAT will replace the call to function free, with hbw_free if it is found in the program file."
               #sed -i.bak "${list2[j]}s/free/hbw_free/" $1
               sed -i.bak "s/$escaped_lhs2/$escaped_rhs2/" $1
               echo " "
               echo " "
           done
           insertDone=$((insertDone+1))
   fi
done
#echo $insertDone

if [[ "$insertDone" > 0 ]]; then
   alreadyExists="$(grep -nE "#include <hbwmalloc.h>" $1|cut -d: -f1)"
   if [[ "$alreadyExists" > 0 ]]; then
      echo "No insertion for include statement needed."
   else 
      echo "#include <hbwmalloc.h>"|cat - $1 > $1_tmp && mv $1_tmp $1
   fi
   list3=($(grep -nw "checkHBMAvailability" $1|cut -d: -f1))
   #echo $list3
   length3=${#list3[@]}
   if [[ "$length3" > 0 ]]; then
         alreadyExists="$(grep -nE "hbw_check_available" $1|cut -d: -f1)"
         if [[ "$alreadyExists" > 0 ]]; then
	   echo "No variable insertion needed."
         else
	   sed -i.bak "${list3}i\\ int checkHBMAvailability=hbw_check_available(); \\" $1
         fi
   fi

   #echo -e "#include <hbwmalloc.h>\n$(cat $1)" > $1
   rm $1.bak
fi

echo " "
echo "${bold}Program modification is complete. The modified program is presented below.${normal}"
echo " "
#cat $1
grep -E --color 'hbw_*|#include <hbwmalloc.h>|if \(checkHBMAvailability == 0\){|int checkHBMAvailability=hbw_check_available\(\);|} else{|$' $1

