#include <fstream>
#include <iostream>
#include <string>
#include <sstream>

using namespace std;

struct clusterMode {
  string mode;
  string pinThreads;
  string allocation;
};

struct progData {
  bool isNUMA_aware; //true if nested omp or omp+MPI
  string parallel;
  string mem; //this is the memory allocation the program will use (DDR4, HBM, or selective)
};  

//determines the recommended cluster mode
void whichClusterMode(progData &prog, clusterMode &cMode){
  if(!prog.isNUMA_aware){ //program is not either an omp or hybrid omp+mpi program
    cMode.mode="Quadrant";
  }
  else {
    if(prog.parallel == "omp"){ //Nested OpenMP with special environment variable settings
      cMode.mode="SNC-4";
      cMode.pinThreads = "You must pin threads to the NUMA nodes prior to executing the program."
			 "\n You should set OMP_NESTED, OMP_NUM_THREADS, OMP_PLACES, and OMP_PROC_BIND."
 			 "\nThese variables can be set as follows:"
                         "\nexport OMP_NESTED=1" 
                         "\nexport OMP_NUM_THREADS=4,64"
                         "\nexport OMP_PLACES=`numactl -H | grep cpus | awk '(NF>3) {for (i = 4; i <= NF; i++) printf \"%d,\", $i}' | sed 's/.$//'` "
                         "\nexport OMP_PROC_BIND=spread,close"; 
      if(prog.mem == "DDR4" || prog.mem == "selective") //all data to DDR4 or using memkind to partition
        //for selective, first touch works with memkind out-of-box as long as threads have been pinned (as per above)
        cMode.allocation = "Use first touch policy. No special execution commands or changes needed, execute as usual.";
      if(prog.mem == "HBM") //entire application in HBM
        cMode.allocation = "numactl -m 4,5,6,7 ./run-app";
    }
    else {
      cMode.mode="SNC-4";
      cMode.pinThreads="";
      //Ritu: change mpirun to ibrun 
      if(prog.mem == "DDR4" || prog.mem == "selective") //all data to DDR4 or using memkind to partition
        cMode.allocation = "mpirun -host knl -np N ./run-app"
                       "\nwhere N is the number of processes per node"; 
      //Note from Ritu: change the "HMB" in the following line to "HBM". Also check the mpirun command 
      if(prog.mem == "HBM") //entire application in HBM
        cMode.allocation = "mpirun -np N -host knl numactl -m 4,5,6,7 ./run-app"
                           "\nwhere N is the number of processes per node"; 
    }
  }
}

void printReport(ofstream &outFile, progData prog, clusterMode cMode, string const PROG_NAME){
  outFile << "----- " << PROG_NAME << " Recommendations -----";
  outFile << "\n Clustering mode to use: " << cMode.mode;
  if(prog.isNUMA_aware){
    if(prog.parallel == "omp")
      outFile << "\n"<< cMode.pinThreads << "\n";   
    outFile << "\n"<< cMode.allocation << "\n";
  }
  outFile << "\n ----- End ICAT report for " << PROG_NAME << " -----\n";
}

int main(int argc, char *argv[]) {
  progData prog;
  clusterMode cMode;
  string const PROG_NAME = argv[1];

  //opening input and output files
  ostringstream ossIn;
  ossIn << "logs/" << PROG_NAME << "_cluster_data.txt";
  string fileIn = ossIn.str();

  ostringstream ossOut;
  ossOut << "reports/" << PROG_NAME << "_clustering_advisor_report.txt";
  string fileOut = ossOut.str();

  ifstream inFile;
  inFile.open (fileIn.c_str());
  if (!inFile){
     cerr << "File not located." << endl;
     return -1;
  }

  ofstream outFile;
  outFile.open (fileOut.c_str());
  if (!outFile){
     cerr << "Unable to create output file." << endl;
     return -1;
  }

  //read input from file
  inFile >> prog.isNUMA_aware;
  inFile >> prog.parallel;
  inFile >> prog.mem;

  whichClusterMode(prog, cMode);
  printReport(outFile, prog, cMode, PROG_NAME);

  inFile.close();
  outFile.close();

return 0;
}

  


  


