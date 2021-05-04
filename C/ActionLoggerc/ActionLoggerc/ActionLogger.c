#include <stdio.h>
#include <sys/stat.h>

void log(const char* s)
{
    FILE *outfile;
    struct stat st;
    if (stat("action_logs.txt", &st) == 0) {
        outfile = fopen("action_logs.txt", "a");
        fprintf(outfile, "\n%s", s);
    }
    else {
        outfile = fopen("action_logs.txt","w");
        fprintf(outfile, "%s", s);
    }
    fclose(outfile);

}

main()
{
    log("First message");
    log("Second message");
}