package com.utsa.eager.controller;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;						  
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;									 
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;						   
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.ModelAttribute;															
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.utsa.eager.model.GAapp;										   
import com.utsa.eager.model.Token;
import com.utsa.eager.model.UsageMetric;
import com.utsa.eager.model.UsageMetricId;
import com.utsa.eager.service.GAappService;									   
import com.utsa.eager.service.TokenService;
import com.utsa.eager.service.UsageMetricService;
import com.utsa.eager.service.UserService;
import com.utsa.eager.utils.CryptoException;
import com.utsa.eager.utils.Decryptlog;
import com.utsa.eager.utils.JsonUtils;

@Controller
public class HomeController {

	
	private static String UPLOAD_FOLDER_JAVA = "/tmp/java/";	
	private static String UPLOAD_FOLDER_CPP = "/tmp/cpp";
	private static String DECRYPTED_FILES_DIR = "/tmp/decryptedfiles";
	private static String CPP_DECRYPTER = "/app/decrypt1 "; // always keep a space at the end
	private static String CSV_PATH = "/tmp/csv/GA/";
	private static final int BUFFER_SIZE = 4096;
	
	

	@Autowired
	private Decryptlog dc;
		
	@Autowired
	private JsonUtils util;
	
	@Autowired
	private UserService userService;
	
	@Autowired
	private UsageMetricService usageMetricService;
	
	@Autowired
	private TokenService tokenService;
	
	@Autowired
	private GAappService gaAppService;

	public void parseJsonFilesJava(String dir) throws IOException, ParseException, CryptoException
	{
	    try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) 
	    {
	        for (Path path : stream) 
	            if (!Files.isDirectory(path)) 
	            {
	            	dc.decryptMAClogfile(path.toString());	            	
	            }
	    }
	    catch (Exception e) {
			// TODO: handle exception
	    	e.printStackTrace();
		}
	
	}
    public static String downloadFile(String fileURL, String saveDir)
            throws IOException {
        URL url = new URL(fileURL);
        HttpURLConnection httpConn = (HttpURLConnection) url.openConnection();
        int responseCode = httpConn.getResponseCode();
        String fileName="";
 
        // always check HTTP response code first
        if (responseCode == HttpURLConnection.HTTP_OK) {
            //String fileName = "";
            String disposition = httpConn.getHeaderField("Content-Disposition");
            String contentType = httpConn.getContentType();
            int contentLength = httpConn.getContentLength();
 
            if (disposition != null) {
                // extracts file name from header field
                int index = disposition.indexOf("filename=");
                if (index > 0) {
                    fileName = disposition.substring(index + 10,
                    		disposition.indexOf("csv")+3);
                }
            } else {
                // extracts file name from URL
                fileName = fileURL.substring(fileURL.lastIndexOf("/") + 1,
                        fileURL.length());
            }
 
//            System.out.println("Content-Type = " + contentType);
//            System.out.println("Content-Disposition = " + disposition);
//            System.out.println("Content-Length = " + contentLength);
//            System.out.println("fileName = " + fileName);
 
            // opens input stream from the HTTP connection
            InputStream inputStream = httpConn.getInputStream();
            String saveFilePath = saveDir + File.separator + fileName;
             
            // opens an output stream to save into file
            FileOutputStream outputStream = new FileOutputStream(saveFilePath);
 
            int bytesRead = -1;
            byte[] buffer = new byte[BUFFER_SIZE];
            while ((bytesRead = inputStream.read(buffer)) != -1) {
                outputStream.write(buffer, 0, bytesRead);
            }
 
            outputStream.close();
            inputStream.close();
 
            //System.out.println("File downloaded");
        } else {
            System.out.println("No file to download. Server replied HTTP code: " + responseCode);
        }
        httpConn.disconnect();
        return fileName;
    }
    
    @RequestMapping("/gaapp")
    public String getappGAform(Model model) {
    	GAapp gaApp=new GAapp();
    	model.addAttribute("gaApp",gaApp);    	
    	return "newGAapp";
    }
    
    @RequestMapping(value="/addgaapp", method=RequestMethod.POST)
    public String submitForm(@ModelAttribute("gaApp") GAapp app, HttpServletRequest request) {
		String ipAddress =  request.getRemoteAddr();
		int id=userService.addorgetUser(ipAddress);
		app.setUserid(id);
    	gaAppService.addGAApp(app);
        return "redirect:/";
    }
		
	public void parseGACSVfiles() throws InterruptedException,IOException {		
		//String ipAddress =  request.getRemoteAddr();
		//int id=userService.addorgetUser(ipAddress);
		List<GAapp> apps= gaAppService.findAllGAProjects();
		for(GAapp app:apps) 
		{
			String fileURL=app.getUrl();
			String command = "mkdir -p "+CSV_PATH;
            Process process = Runtime.getRuntime().exec(command);
        	int exitValue = process.waitFor();
            if (exitValue != 0) {
            	System.out.println("Abnormal process termination for Uploadd JAVA dir creation");
            }
			String fileName=downloadFile(fileURL, CSV_PATH);
			int numOfUsers=-1;
			boolean foundUsers=false;
			int cnt=0;

			String line = "";
			try   
			{  
				
				BufferedReader br = new BufferedReader(new FileReader(Paths.get(CSV_PATH+"/"+fileName).toString()));  
				while ((line = br.readLine()) != null) 
				{
					if(cnt++ == 0) continue;
					
					String[] words=line.split(",");
					if(words.length==0) 
						continue;

					if(words[0].equals("Users")) { 
						foundUsers=true;
						continue;
					}
					if(foundUsers) 
					{
						numOfUsers=Integer.parseInt(words[0]);
						break;
					}
					
				}  
			}   
			catch (IOException e)   
			{  
				e.printStackTrace();  
			}  
			
			UsageMetric um=new UsageMetric();
			um.setUserid(app.getUserid());
			um.setApplication(app.getApplication());
			um.setMetric("Users");
			um.setUsage(numOfUsers);
			
			usageMetricService.updateUsage(um);	
		}
	}


	public void parseJsonFiles(String dir) throws IOException
	{
	    try (DirectoryStream<Path> stream = Files.newDirectoryStream(Paths.get(dir))) 
	    {
	        for (Path path : stream) 
	            if (!Files.isDirectory(path)) 
	            {
	            	util.logJsonFile(path.toString());	            	
	            }
	    }
	    catch (Exception e) {
			// TODO: handle exception
	    	e.printStackTrace();
		}
	
	}
	
	@RequestMapping(value= {"/","/home"}, method=RequestMethod.GET)	
	public String listUsage(Model mod) throws InterruptedException,IOException{
		List<UsageMetric> usagelist = usageMetricService.getAppUsage();
		parseGACSVfiles();
		mod.addAttribute("usagelist", usagelist);
		return "appusage";
	}
	
	
	////
	//// Tokens for security
	////
	@RequestMapping(value= {"/getToken","/getToken/{macaddress}"}, method=RequestMethod.GET)
	@ResponseBody
	public String getToken(HttpServletRequest request,@PathVariable(required=false) String macaddress){
		String token=null;
		if(macaddress!=null) {
			int id=userService.addorgetUser(macaddress);
			token=tokenService.assignToken(id);
		}
		else 
		{
			String ipAddress =  request.getRemoteAddr();
			int id=userService.addorgetUser(ipAddress);
			token=tokenService.assignToken(id);
		}
		return token;
	}
	
	////
	//// Web based logs for GET and POST requests
	////
	@SuppressWarnings("rawtypes")
	@RequestMapping(value= {"/log/{token}/{application}/{metric}","/log/{token}/{application}"})
	public ResponseEntity addLogget(@PathVariable(name="token") String tok,@PathVariable(name="application") String application, @PathVariable(name="metric",required = false) String metric,HttpServletRequest request) throws JSONException {		
		
		String ipAddress =  request.getRemoteAddr();
		int id=userService.addorgetUser(ipAddress);
		Token token=new Token();
		token.setUserid(id);
		token.setValue(tok);
		if(!tokenService.verifyToken(token))
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		
		UsageMetric um=new UsageMetric();
		um.setUserid(id);
		um.setApplication(application);
		if(metric!=null)
			um.setMetric(metric);
		else
			um.setMetric("usage");			
		usageMetricService.incrementUsage(um);
		return new ResponseEntity(HttpStatus.OK);	
	}
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/log", method=RequestMethod.POST)
	public ResponseEntity addLogpost(@RequestBody String payload,HttpServletRequest request) throws JSONException {		
		
		String ipAddress =  request.getRemoteAddr();
		int id=userService.addorgetUser(ipAddress);
		//from weburl post request with JSON payload
		final JSONObject obj = new JSONObject(payload);
		
		UsageMetric um=new UsageMetric();
		um.setUserid(id);
		um.setApplication(obj.getString("application"));
		if(obj.has("metric"))
			um.setMetric(obj.getString("metric"));
		else
			um.setMetric("usage");
		String tok=obj.getString("token");
		
		Token token=new Token();
		token.setUserid(id);
		token.setValue(tok);
		System.out.println("token: "+tok);
		if(!tokenService.verifyToken(token))
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		
		usageMetricService.incrementUsage(um);
		return new ResponseEntity(HttpStatus.OK);
	}
	
	////
	//// Logs from Desktop apps
	////
	
    @SuppressWarnings("rawtypes")
	@RequestMapping(value= {"/upload/java/{token}","/upload/java/{token}/{macaddress}"},method=RequestMethod.POST)  
    public ResponseEntity singleFileUpload(@RequestParam("file") MultipartFile file,@PathVariable(name="token") String tok,@PathVariable(name="macaddress",required = false) String macaddress,HttpServletRequest request) throws InterruptedException,IOException, ParseException, CryptoException{
    	int id=0;
    	if(macaddress==null) 
    	{
    		String ipAddress =  request.getRemoteAddr();
    		id=userService.addorgetUser(ipAddress);
		}
    	else 
    	{
    		id=userService.addorgetUser(macaddress);
    	}
		Token token=new Token();
		token.setUserid(id);
		token.setValue(tok);
		if(!tokenService.verifyToken(token))
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

        if (file.isEmpty()) new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        try
        {
        	String command = "mkdir -p "+UPLOAD_FOLDER_JAVA;
            Process process = Runtime.getRuntime().exec(command);
        	int exitValue = process.waitFor();
            if (exitValue != 0) {
            	System.out.println("Abnormal process termination for Uploadd JAVA dir creation");
            }
            
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_FOLDER_JAVA+file.getOriginalFilename());
            Files.write(path, bytes);
        }
        catch (IOException e) 
        {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        parseJsonFilesJava(UPLOAD_FOLDER_JAVA);
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @SuppressWarnings("rawtypes")
	@RequestMapping(value= {"/upload/cpp/{token}","/upload/cpp/{token}/{macaddress}"},method=RequestMethod.POST) 
    public ResponseEntity singleFileUploadcpp(@RequestParam("file") MultipartFile file,@PathVariable(name="token") String tok,@PathVariable(name="macaddress",required = false) String macaddress,HttpServletRequest request) throws InterruptedException,IOException, ParseException, CryptoException{
    
    	int id=0;
    	if(macaddress==null) 
    	{
    		String ipAddress =  request.getRemoteAddr();
    		id=userService.addorgetUser(ipAddress);
		}
    	else 
    	{
    		id=userService.addorgetUser(macaddress);
    	}
    	
    	Token token=new Token();
    	token.setUserid(id);
    	token.setValue(tok);
    	//System.out.println("token CPP: "+tok);
    	if(!tokenService.verifyToken(token))
    		return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

    	//System.out.println("Verification successfull for CPP");
        if (file.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

        try
        {
        	String command = "mkdir -p "+UPLOAD_FOLDER_CPP;
            Process process = Runtime.getRuntime().exec(command);
        	int exitValue = process.waitFor();
            if (exitValue != 0) {
            	System.out.println("Abnormal process termination for Uploadd CPP dir creation");
            }
        	
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_FOLDER_CPP+file.getOriginalFilename());
            Files.write(path, bytes);

            // decrypt the file
            String command1 = CPP_DECRYPTER + path.toString();
            Process process1 = Runtime.getRuntime().exec(command1);
            exitValue = process1.waitFor();
            if (exitValue != 0) {
            	System.out.println("Abnormal process termination for CPP file decryption");
            }
            
            String command2 = "mkdir -p "+DECRYPTED_FILES_DIR;
            Process process2 = Runtime.getRuntime().exec(command2);
        	exitValue = process2.waitFor();
            if (exitValue != 0) {
            	System.out.println("Abnormal process termination for Decrypted CPP files dir creation");
            }

            // move the file to add to the database
            String command3 = "mv "+path.toString()+" "+DECRYPTED_FILES_DIR;
            Process process3 = Runtime.getRuntime().exec(command3);

        }
        catch (IOException e) 
        {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        parseJsonFiles(DECRYPTED_FILES_DIR);
        return new ResponseEntity<>(HttpStatus.OK);
    }
}
