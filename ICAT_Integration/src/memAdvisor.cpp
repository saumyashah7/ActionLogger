#include <fstream>
#include <iostream>
#include <string>
#include <sstream>
#include <stdlib.h> 

using namespace std;

struct memMode {
  string mode;
  string allocation;
};

struct progData {
  float vMem;
  int instructions;

  float cacheAccesses[4];
  float totalCacheAccesses;
  float cacheMisses[4];
  float totalCacheMisses;
  float cacheMissRate; // totalCacheMisses/totalCacheAccesses

  /* cacheAccesses Array contents
     0 float L1dcacheLoads;
     1 float L1dcacheStores;
     2 float LLCloads;
     3 float LLCstores;
     
     cacheMisses Array contents
     0 float L1dcacheLoadMisses;
     1 float L1dcacheStoreMisses;
     2 float LLCloadMisses; 
     3 float LLCstoreMisses; */

  float runtime;
  bool BWCfound;
};  

//need to see how code benefits from mem opt in all three modes


//determines the recommended memory mode and allocation to use
void whichMemMode(progData &prog, memMode &mMode){
  if(prog.vMem < 0.001){ //can fit into 1MB l2 cache, no need to use HBM
    mMode.mode="Application fits into L2 cache"
               "\nMode to use: Flat Mode with default settings";
    mMode.allocation="Memory Allocation: DDR4"
                     "\nNo changes needed, execute application normally"; //will default to DDR4
  }
  else if(prog.vMem < 16){ //can fit into HBM
    mMode.mode="Application fits into HBM.\n"
               "\nMode to use: If numactl is available, use the Flat-Mode with all allocations to HBM.\n"
               "\nIf numactl is not available, then use the Cache-Mode. However, note that the cache misses in the Cache-Mode are more expensive than reading data from DDR4 in Flat-Mode.\n";
    mMode.allocation="Memory Allocation: HBM"
                     "\nTo execute the application in FLat-Mode: Use command < numactl --membind=1 ./run-app> if it is serial, or < ibrun --membind=1 ./run-app > if it is parallel. \n" 
                     "\nTo execute the application in Cache-mode: Use the command that you normally use, that is, < ./run-app > if it is serial or < ibrun ./run-app > if it is parallel. \n"
                     "\nIn general, to determine the <NUMA_NODE> in the command < numactl --membind=NUMA_NODE > , run the command < numactl -H > and look for the node without any core";
  }
  else if(prog.BWCfound == true){ //need to partition - use the code transformation script here
     mMode.mode="Memory footprint exceeds size of HBM"
                "\nMode to use: Flat Mode";
     mMode.allocation="Memory Allocation: selective"
                      "\nBandwidth-critical sections of application should be partitioned"
                      "\nSource code modification required to allocate BW-critical components to HBM using memkind"
                      "\nThe code must be compiled with -lmemkind flag"
		      "\nFind all occurances of malloc/calloc/realloc/free and replace with hbw_malloc/hbw_calloc/hbw_realloc";
		      cout << "\nWould you like ICAT to help you with the code transformation to take advanatage of the Memkind interface?\n";
  } 
  else{  //unable to find BW-critical sections < 16 GB to partition
    if (prog.cacheMissRate < 0.05){
      mMode.mode="Memory footprint exceeds size of HBM, ICAT unable to find BW-critical sections to partition,"
                 " application exhibits good cache performance"
                 "\nMode to use: Cache Mode";
      mMode.allocation="Memory Allocation: HBM"
                       "\nNo changes needed, execute application normally";
    }
    else{
      mMode.mode="Memory footprint exceeds size of HBM, ICAT unable to find BW-critical sections to partition,"
                 " application exhibits poor cache performance"
                 "\nMode to use: Flat Mode with all allocations to DDR4";
      mMode.allocation="Memory Allocation: DDR4"
                       "\nExecute application using numactl --membind=0 ./run-app";
    }
  }
}

void printReport(ofstream &outFile, progData prog, memMode mMode, string const PROG_NAME){
  outFile << "----- " << PROG_NAME << " Characteristics -----\n";
  outFile << "Memory usage: "<< prog.vMem << "\t";
  //outFile << "Number of instructions: "<< prog.instructions << "\t";
  outFile << "Cache Miss Rate: "<< prog.cacheMissRate;
  outFile << "\n \n----- Recommendations -----\n";
  outFile << "\n" << mMode.mode;
  outFile << "\n"<< mMode.allocation << "\n";
  if(prog.BWCfound == true){
    outFile << "It is suggested to allocate the following memory objects to HBM: \n";
    ostringstream ossIn;
    ossIn << PROG_NAME << "_memObj.txt";
    string filename = ossIn.str();
    ifstream file;
    file.open (filename.c_str());
    if (!file){
     cerr << "File not located. 2" << endl;
    }
    string line;
    while (getline(file, line))
       outFile << line << "\n";
    file.close();
  }
  outFile << "\n ----- End ICAT report for " << PROG_NAME << " -----\n";  
}

int main(int argc, char *argv[]) {
  progData prog;
  memMode mMode;
  string const PROG_NAME = argv[1];

  //opening input and output files
  ostringstream ossIn;
  ossIn << "icat/logs/" << PROG_NAME << "_data.txt";
  string fileIn = ossIn.str();
 
  //cout << fileIn << "\n";
 
  ostringstream ossOut;
  ossOut << "icat/reports/" << PROG_NAME << "_memory_advisor_report.txt";
  string fileOut = ossOut.str();

  //cout << fileOut << "\n";
  //system("pwd"); 

  ifstream inFile;
  inFile.open (fileIn.c_str());
  if (!inFile){
     cerr << "File not located. 1" << endl;
     return -1;
  }

  ofstream outFile;
  outFile.open (fileOut.c_str());
  if (!outFile){
     cerr << "Unable to create output file." << endl;
     return -1;
  }

  //read input from file
  float KB;
  inFile >> KB;
  prog.vMem = KB/1024.0/1024.0;
  inFile >> prog.instructions;

  for(int i=0; i<4; i++){
     inFile >> prog.cacheAccesses[i];
     prog.totalCacheAccesses += prog.cacheAccesses[i];
  }

  for(int i=0; i<4; i++){
     inFile >> prog.cacheMisses[i];
     prog.totalCacheMisses += prog.cacheMisses[i];
  }

  inFile >> prog.runtime;
  inFile >> prog.BWCfound;

 prog.cacheMissRate = prog.totalCacheMisses/prog.totalCacheAccesses;
  
  whichMemMode(prog, mMode);
  printReport(outFile, prog, mMode, PROG_NAME);
  //cout << mMode.mode ;
  //string strLookUp ("Source code modification");
  //the string below is just for testing purposes. For real runs, use the string above.

  //string strLookUp ("Flat Mode with all allocations to HBM");
  //size_t found = (mMode.mode).find(strLookUp);
  //int toTransform=0;
  //if (found!=std::string::npos){
    //std::cout << "string found at: " << found << '\n';
    //cout << "\nICAT can transform your source code to allocate certain bandwidth critical memory objects on HBM."<<  "\n";
    //cout << "\nIf you would like to proceed with code transformation using ICAT, please press 1.\n" << "\n" ;
    //cin >> toTransform;
    //cout << "\nYou entered: " << toTransform << "\n" ;
    //if(toTransform==1){
       // string command = "./scriptHBW2.sh ";
        //command += argv[1];
	//system("command");
    //}
  //}


/* TODO: get source code sections to output to user */

  inFile.close();
  outFile.close();

return 0;
}

  


  


