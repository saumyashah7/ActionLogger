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

namespace
{
	std::size_t callback(
			const char* in,
			std::size_t size,
			std::size_t num,
			std::string* out)
	{
		const std::size_t totalBytes(size * num);
		out->append(in, totalBytes);
		return totalBytes;
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

std::string ltrim(const std::string& s)
{
    size_t start = s.find_first_not_of(WHITESPACE);
    return (start == std::string::npos) ? "" : s.substr(start);
}

std::string rtrim(const std::string& s)
{
    size_t end = s.find_last_not_of(WHITESPACE);
    return (end == std::string::npos) ? "" : s.substr(0, end + 1);
}

std::string trim(const std::string& s) {
    return rtrim(ltrim(s));
}

std::vector<std::string> split(std::string str, char delimiter) {
    std::vector<std::string> internal;
    std::stringstream ss(str); // Turn the string into a stream.
    std::string tok;

    while (getline(ss, tok, delimiter)) {
        internal.push_back(tok);
    }
    return internal;
}

std::string encrypt(const std::string& input)
{
    std::string cipher;
    auto aes = CryptoPP::AES::Encryption(key.data(), key.size());
    auto aes_ecb = CryptoPP::ECB_Mode_ExternalCipher::Encryption(aes);
   
    CryptoPP::StringSource ss(
        input, 
        true, 
        new CryptoPP::StreamTransformationFilter(
            aes_ecb, 
            new CryptoPP::Base64Encoder(
                new CryptoPP::StringSink(cipher)
            )
        )
    );

    return trim(cipher);
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

template<typename T>

std::string toString(const T &t) 
{
    std::ostringstream oss;
    oss << t;
    return oss.str();
}

time_t fromString(std::string& s ) {

    std::istringstream stream( s );
    time_t t;
    stream >> t;
    return t;
}

bool checkTimestamp(std::string timest)
{
	time_t now;
	time(&now);
	auto jsontime=fromString(timest);

	double interval = difftime(now,jsontime);
	int threshold = 10;//24*60*60;
	if(interval>threshold){
		std::cout<<"send file"<<std::endl;
		return true;
	}
	return false;
}

std::string getToken()
{	
	    const std::string url("https://eagerapp1.herokuapp.com/getToken");
	    
	    CURL* curl = curl_easy_init();
	    
	    // Set remote URL.
	    curl_easy_setopt(curl, CURLOPT_URL, url.c_str());
	    
	    // Don't bother trying IPv6, which would increase DNS resolution time.
	    curl_easy_setopt(curl, CURLOPT_IPRESOLVE, CURL_IPRESOLVE_V4);
	    
	    // Don't wait forever, time out after 10 seconds.
	    curl_easy_setopt(curl, CURLOPT_TIMEOUT, 10);
	    
	    // Response information.
	    long httpCode(0);
	    std::unique_ptr<std::string> httpData(new std::string());
	    
	    // Hook up data handling function.
	    curl_easy_setopt(curl, CURLOPT_WRITEFUNCTION, callback);
	    
	    // Hook up data container (will be passed as the last parameter to the
	    // callback handling function).  Can be any pointer type, since it will
	    // internally be passed as a void pointer.
            curl_easy_setopt(curl, CURLOPT_WRITEDATA, httpData.get()); 

	    // Run our HTTP GET command, capture the HTTP response code, and clean up.
            curl_easy_perform(curl);
            curl_easy_getinfo(curl, CURLINFO_RESPONSE_CODE, &httpCode);
            curl_easy_cleanup(curl);

	    if (httpCode == 200)
	    {
		    //std::cout << "\nGot successful response from " << url << std::endl;
		    return *httpData.get();
	    }
	    return "tokennotfound";

}

void sendPost()
{
	std::string IP = getIP();
	std::string fname = "actions_" + getIP() + ".json";
	char* filename = const_cast<char*>(fname.c_str());

	std::string contents;
	std::ifstream in(filename, std::ios::in | std::ios::binary);

	if (in)
	{
		in.seekg(0, std::ios::end);
		contents.resize(in.tellg());
		in.seekg(0, std::ios::beg);
		in.read(&contents[0], contents.size());
		in.close();
	}

	CURL *curl;
	CURLcode res;

	struct curl_httppost *formpost = NULL;
	struct curl_httppost *lastptr = NULL;
	struct curl_slist *headerlist = NULL;
	static const char buf[] =  "Expect:";
	std::string strurl= "https://eagerapp1.herokuapp.com/upload/cpp/"+getToken();
	char* url = const_cast<char*>(strurl.c_str());

	curl_global_init(CURL_GLOBAL_ALL);

	// set up the header
	curl_formadd(&formpost,
	    &lastptr,
	    CURLFORM_COPYNAME, "cache-control:",
	    CURLFORM_COPYCONTENTS, "no-cache",
	    CURLFORM_END);

	curl_formadd(&formpost,
	    &lastptr,
	    CURLFORM_COPYNAME, "content-type:",
	    CURLFORM_COPYCONTENTS, "multipart/form-data",
	    CURLFORM_END);
	
	curl_formadd(&formpost, &lastptr,
	    CURLFORM_COPYNAME, "file",  // <--- the (in this case) wanted file-Tag!
	    CURLFORM_BUFFER, filename,
	    CURLFORM_BUFFERPTR, contents.data(),
	    CURLFORM_BUFFERLENGTH, contents.size(),
	    CURLFORM_END);
	
	curl = curl_easy_init();
	
	headerlist = curl_slist_append(headerlist, buf);
	if (curl) {
	
	    curl_easy_setopt(curl, CURLOPT_URL, url);
	    curl_easy_setopt(curl, CURLOPT_HTTPPOST, formpost);
	
	    res = curl_easy_perform(curl);
	    /* Check for errors */
	    if (res != CURLE_OK)
	        fprintf(stderr, "curl_easy_perform() failed: %s\n",
	            curl_easy_strerror(res));
	
	    curl_easy_cleanup(curl);
	    curl_formfree(formpost);
	    curl_slist_free_all(headerlist);
	}
}

void log(std::string s)
{
    std::vector<std::string> str = split(s, ',');
    std::string IP = getIP();
    std::string fname = "actions_" + IP + ".json";
    char* filename = const_cast<char*>(fname.c_str());

    if(str.size()==1)
    {
	    std::string application = trim(str.at(0));
	    json logfile;
	    struct stat st;
	    if (stat(filename, &st) == 0) 
	    {
	        std::ifstream file(filename);
	        json j = json::parse(file);
	        std::string strcount = decrypt(j[encrypt("usage")].get<std::string>());
	        int count = std::stoi(strcount);
        	j[encrypt("usage")] = encrypt(std::to_string(++count));
		if(checkTimestamp(decrypt(j[encrypt("EAGERlastsentdatetime")].get<std::string>())))
		{
			time_t now;
			time(&now);
			sendPost();
			j[encrypt("EAGERlastsentdatetime")]=encrypt(toString(now));
		}
	        std::ofstream outfile(filename);
        	outfile << j << std::endl;
	    }
	    else 
	    {
	        logfile[encrypt("software-name")] = encrypt(application);
	        logfile[encrypt("usage")] = encrypt("1");
		time_t now;
		time(&now);
		std::cout<<now<<std::endl;
		logfile[encrypt("EAGERlastsentdatetime")] = encrypt(toString(now));
	        std::ofstream outfile(filename);
        	outfile << logfile << std::endl;
	    }
    }
    else
    {
	    std::string application = trim(str.at(0));
	    std::string metric = trim(str.at(1));
	    json logfile;
	    struct stat st;
	    if (stat(filename, &st) == 0) 
	    {
	        std::ifstream file(filename);
	        json j = json::parse(file);
	        std::string strcount = decrypt(j[encrypt("usage")].get<std::string>());
	        int count = std::stoi(strcount);
        	j[encrypt("usage")] = encrypt(std::to_string(++count));

	        if (j.contains(encrypt(metric)))
	        {
        	    int count = std::stoi(decrypt(j[encrypt(metric)].get<std::string>()));
	            j[encrypt(metric)] = encrypt(std::to_string(++count));
        	}
	        else
        	{
	            j[encrypt(metric)] = encrypt("1");
        	}

                if(checkTimestamp(decrypt(j[encrypt("EAGERlastsentdatetime")].get<std::string>())))
                {
                        time_t now;
                        time(&now);
			sendPost();
                        j[encrypt("EAGERlastsentdatetime")]=encrypt(toString(now));
                }

	        std::ofstream outfile(filename);
        	outfile << j << std::endl;
	    }
	    else 
	    {
	        logfile[encrypt("software-name")] = encrypt(application);
	        logfile[encrypt("usage")] = encrypt("1");
		logfile[encrypt(metric)] = encrypt("1");
		time_t now;
                time(&now);
                std::cout<<now<<std::endl;
                logfile[encrypt("EAGERlastsentdatetime")] = encrypt(toString(now));
	        std::ofstream outfile(filename);
        	outfile << logfile << std::endl;
	    }
    }

}

