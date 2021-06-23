#include <vector>
#include <iostream>
#include <fstream>
#include <sstream>
#include <crypto++/cryptlib.h>
#include <crypto++/modes.h>
#include <crypto++/aes.h>
#include <crypto++/filters.h>
#include <crypto++/osrng.h>
#include <crypto++/base64.h>
#include "json.hpp"
#include <sys/stat.h>
#include <ctime>
#include <cstring>

#include <stdlib.h>
#include <unistd.h>
#include <errno.h>
#include <netdb.h>
#include <sys/types.h>
#include <sys/socket.h>
#include <netinet/in.h>
#include <arpa/inet.h>

#include <curlpp/cURLpp.hpp>
#include <curlpp/Easy.hpp>
#include <curlpp/Options.hpp>
#include <curlpp/Exception.hpp>

using json = nlohmann::json;

const std::string WHITESPACE = " \n\r\t\f\v";
std::vector<uint8_t> key{1,1,2,2,3,3,4,4,5,5,6,6,7,7,8,8};

void check_host_name(int hostname) { //This function returns host name for local computer
   if (hostname == -1) {
      perror("gethostname");
      exit(1);
   }
}

void check_host_entry(struct hostent * hostentry) { //find host info from host name
   if (hostentry == NULL){
      perror("gethostbyname");
      exit(1);
   }
}

void IP_formatter(char *IPbuffer) { //convert IP string to dotted decimal format
   if (NULL == IPbuffer) {
      perror("inet_ntoa");
      exit(1);
   }
}

std::string getIP(){
    char host[256];
    char *IP;
    struct hostent *host_entry;
    int hostname;
    
    hostname = gethostname(host, sizeof(host)); //find the host name
    check_host_name(hostname);
    host_entry = gethostbyname(host); //find host information
    check_host_entry(host_entry);
    IP = inet_ntoa(*((struct in_addr*) host_entry->h_addr_list[0])); //Convert into IP string
    //printf("Current Host Name: %s\n", host);
    //printf("Host IP: %s\n", IP);
    std::string ipaddr(IP);
    return ipaddr;
}

std::string decrypt(const std::string& cipher_text) 
{
    std::string plain_text;
    auto aes = CryptoPP::AES::Decryption(key.data(), key.size());
    auto aes_ecb = CryptoPP::ECB_Mode_ExternalCipher::Decryption(aes);
   
    CryptoPP::StringSource ss(
        cipher_text, 
        true, 
        new CryptoPP::Base64Decoder(
            new CryptoPP::StreamTransformationFilter(
                aes_ecb, 
                new CryptoPP::StringSink(plain_text)
            )
        )
    );

    return plain_text;
}

void decryptLog(char* filename)
{
	std::ifstream file(filename);
        json j = json::parse(file), outj;
	for (auto it = j.begin(); it != j.end(); ++it){
		outj[decrypt(it.key())]=decrypt(it.value());
	}
	std::ofstream outfile(filename);
	outfile << outj << std::endl;
}

int main(int argc, char** argv)
{
	//std::cout << argv[1];
	//std::string fname = "actions_" + getIP() + ".json";
	//char* filename = const_cast<char*>(fname.c_str());
	decryptLog(argv[1]);
}
