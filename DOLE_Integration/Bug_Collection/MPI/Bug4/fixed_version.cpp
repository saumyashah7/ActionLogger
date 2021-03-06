#include <stdio.h>
#include <mpi.h> 

int main()
{
  int size;
  int rank;
  MPI_Init(NULL,NULL);
  MPI_Comm_size(MPI_COMM_WORLD,&size);
  MPI_Comm_rank(MPI_COMM_WORLD,&rank);
  int arr [9];
  int rev[9];
  int range = 5/size;
  int start = rank*range; 
  int end = start + range;
  int sendcount=(rank==size-1) ? 1: 2;
  int recvcounts[size]; int displs[size];
  for (int i=0;i<size;i++) {
    recvcounts[i]=(i==size-1)? 1: 2;
    displs[i]=(i==0)? 0: recvcounts[i-1];
  }
  for (int i = start; i < end; i++)
  {
    arr[i*2]=1; if (i < 4) {
    arr[i*2+1] =2; }
  }
  MPI_Gatherv(&arr[rank*range *2],sendcount,MPI_INT
  ,rev,displs, recvcounts,MPI_INT,0,
  MPI_COMM_WORLD);
  MPI_Finalize();
  return 0;
}
