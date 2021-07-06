package com.utsa.eager.controller;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;

import com.utsa.eager.model.Token;
import com.utsa.eager.model.UsageMetric;
import com.utsa.eager.service.TokenService;
import com.utsa.eager.service.UsageMetricService;
import com.utsa.eager.service.UserService;
import com.utsa.eager.utils.CryptoException;
import com.utsa.eager.utils.Decryptlog;
import com.utsa.eager.utils.JsonUtils;

@Controller
public class HomeController {

	
	//private static String UPLOAD_FOLDER_JAVA = "/home/json/java/";	
	private static String UPLOAD_FOLDER_JAVA = "/tmp/java/";	
	private static String UPLOAD_FOLDER_CPP = "/tmp/cpp";
	private static String DECRYPTED_FILES_DIR = "/tmp/decryptedfiles";
	private static String CPP_DECRYPTER = "/app/decrypt1 "; // always keep a space at the end
	
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
	
	@RequestMapping(value= {"/","/home}"}, method=RequestMethod.GET)	
	public String listUsage(Model mod) {
		List<UsageMetric> usagelist = usageMetricService.getAppUsage();
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
