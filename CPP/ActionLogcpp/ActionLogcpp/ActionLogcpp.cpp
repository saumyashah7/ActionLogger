// ActionLogcpp.cpp : This file contains the 'main' function. Program execution begins and ends there.
//

#include <iostream>
#include <fstream>

void log(const char* s)
{
    std::ofstream outfile;
    struct stat st;
    if (stat("action_logs.txt", &st) == 0) {
        outfile.open("action_logs.txt", std::ios_base::app); // append instead of overwrite
        outfile << "\n" << s;
    }
    else {
        outfile.open("action_logs.txt");
        outfile << s;
    }
    outfile.close();

}

int main()
{    
    log("First message");
    log("Second message");
    return 0;
}

