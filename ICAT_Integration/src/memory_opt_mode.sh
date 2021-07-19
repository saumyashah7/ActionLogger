#!/bin/bash
bash actionlogger.sh ICAT Memory_optimization_advisor
bold=$(tput bold)
normal=$(tput sgr0)
echo "${bold}The Memory Optimization Advisor is currently unavailable. It is being designed to provide support for the following:"

#eliminating temorary buffers by combining statements
#If the intermediate calculation results are written to a temporary buffer that is read later during the program execution, then eliminate the usage of the data strcuture and move the calculation statements to a point immediately before the calculation results are used.
#if an identity matrix is being used, can it be eliminated to use just the diagonal elements?
echo "1. Eliminating redundant data-structures"

#evaluate if recalculation is faster than memory access
echo "2. Recalcultaing values"

#evaluate if certain data structures are consuming memory for longer than they are being accessed
#If a data-structure is allocated at the beginning of the program but is first accessed very late during the program execution, then move the allocation statement of the data-structure immediately before the statement where it is first accessed. Also move the statement foe its de-allocation immediately after the statement of its last access.
echo "3. Optimizing memory foot-print by evaluating memory lifetime and memory access period ${normal}"

#cache-blocking: blobking buffer to make it fit in HBM


echo " "
