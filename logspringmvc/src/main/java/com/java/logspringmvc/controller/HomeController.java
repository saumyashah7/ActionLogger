package com.java.logspringmvc.controller;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.*;
import java.util.List;
import javax.servlet.http.HttpServletRequest;

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
import com.java.logspringmvc.dao.LogDAO;
import com.java.logspringmvc.dao.TokenDAO;
import com.java.logspringmvc.dao.UsageMetricDAO;
import com.java.logspringmvc.dao.UserDAO;
import com.java.logspringmvc.model.Log;
import com.java.logspringmvc.model.Token;
import com.java.logspringmvc.model.UsageMetric;
import com.java.logspringmvc.util.CryptoException;
import com.java.logspringmvc.util.Decryptlog;
import com.java.logspringmvc.util.JsonUtils;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

@Controller
public class HomeController {
	
	private static String UPLOAD_FOLDER_JAVA = "/home/json/java/";	
	private static String UPLOAD_FOLDER_CPP = "/home/json/cpp/";
	private static String DECRYPTED_FILES_DIR = "/home/decryptedfiles/";

	@Autowired
	private LogDAO logDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private UsageMetricDAO usagemetricDAO;
	
	@Autowired
	private TokenDAO tokenDAO;
	
	@Autowired
	private Decryptlog dc;
		
	@Autowired
	private JsonUtils util;

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
	@RequestMapping(value= {"/","/home"})
	public String listLogs(Model mod) throws IOException, ParseException, CryptoException {
		List<Log> listlogs=logDAO.getLogs(); 
		mod.addAttribute("listLogs", listlogs);
		return "home";
	}

	@RequestMapping(value= {"/appusage"})
	public String listUsage(Model mod) throws IOException, ParseException, CryptoException {
		parseJsonFilesJava(UPLOAD_FOLDER_JAVA);
		parseJsonFiles(DECRYPTED_FILES_DIR);
		List<UsageMetric> usagelist = usagemetricDAO.getAppUsage();  
		mod.addAttribute("usagelist", usagelist);
		return "appusage";
	}

	@RequestMapping(value="/addlog/{datetime}/{application}/{method}/{description}")
	public String addLog(@PathVariable("datetime") String time, @PathVariable("application") String application, @PathVariable("method") String method, @PathVariable("description") String description){
		logDAO.addlog(new Log(time,application,method,description));
		return "redirect:/home";
	}
	
//	@RequestMapping(value="/addlog", method=RequestMethod.POST)
//	public void addLogpost(@RequestBody String payload) throws JSONException {
//	//public void addLogpost(@RequestParam("datetime") String datetime, @RequestParam("application") String application, @RequestParam("method") String method, @RequestParam("description") String description){		
//		//logDAO.addlog(new Log(datetime,application,method,description));
//		
//		//from weburl post request with JSON payload
//		//final JSONObject obj = new JSONObject(payload);
//		//logDAO.addlog(new Log(obj.getString("datetime"),obj.getString("application"),obj.getString("method"),obj.getString("description")));
//		
//	}
	
	
	@SuppressWarnings("rawtypes")
	@RequestMapping(value= {"/log/{token}/{application}/{metric}","/log/{token}/{application}"})
	public ResponseEntity addLogget(@PathVariable(name="token") String tok,@PathVariable(name="application") String application, @PathVariable(name="metric",required = false) String metric,HttpServletRequest request) throws JSONException {		
		
		String ipAddress =  request.getRemoteAddr();
		int id=userDAO.addorgetUser(ipAddress);
		Token token=new Token();
		token.setUserid(id);
		token.setValue(tok);
		if(!tokenDAO.verifyToken(token))
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		
		UsageMetric um=new UsageMetric();
		um.setUserid(id);
		um.setApplication(application);
		if(metric!=null)
			um.setMetric(metric);
		else
			um.setMetric("usage");			
		usagemetricDAO.incrementUsage(um);
		return new ResponseEntity(HttpStatus.OK);	
	}
//	@SuppressWarnings("rawtypes")
//	@RequestMapping(value= {"/log/{application}"})
//	public ResponseEntity addLoggetapponly(@PathVariable(name="application") String application,HttpServletRequest request) throws JSONException {		
//		
//		String ipAddress =  request.getRemoteAddr();
//		UsageMetric um=new UsageMetric();
//		int id=userDAO.addorgetUser(ipAddress);
//		um.setUserid(id);
//		um.setApplication(application);
//		um.setMetric("usage");
//		usagemetricDAO.incrementUsage(um);
//		return new ResponseEntity(HttpStatus.OK);
//		
//		
//	}
	
	//get token first from: http://localhost:8080/logspringmvc/getToken
	@SuppressWarnings("rawtypes")
	@RequestMapping(value="/log", method=RequestMethod.POST)
	public ResponseEntity addLogpost(@RequestBody String payload,HttpServletRequest request) throws JSONException {		
		
		String ipAddress =  request.getRemoteAddr();
		int id=userDAO.addorgetUser(ipAddress);
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
		if(!tokenDAO.verifyToken(token))
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		
		usagemetricDAO.incrementUsage(um);
		return new ResponseEntity(HttpStatus.OK);
		
		
	}

	@RequestMapping(value= {"/getToken","/getToken/{macaddress}"}, method=RequestMethod.GET)
	@ResponseBody
	public String getToken(HttpServletRequest request,@PathVariable(required=false) String macaddress){
		String token=null;
		if(macaddress!=null) {
			int id=userDAO.addorgetUser(macaddress);
			token=tokenDAO.assignToken(id);
		}
		else 
		{
			String ipAddress =  request.getRemoteAddr();
			int id=userDAO.addorgetUser(ipAddress);
			token=tokenDAO.assignToken(id);
		}
		return token;
	}

    @SuppressWarnings("rawtypes")
//	@RequestMapping(value= {"/upload/java/{token}","/upload/java/{token}/{macaddress}"},method=RequestMethod.POST) 
    @RequestMapping(value= "/upload/java/{token}/{macaddress}",method=RequestMethod.POST) 
    public ResponseEntity singleFileUpload(@RequestParam("file") MultipartFile file,@PathVariable(name="token") String tok,@PathVariable(name="macaddress") String macaddress,HttpServletRequest request) {
    	int id=0;
    	if(macaddress==null) 
    	{
    		String ipAddress =  request.getRemoteAddr();
    		id=userDAO.addorgetUser(ipAddress);
		}
    	else 
    	{
    		id=userDAO.addorgetUser(macaddress);
    	}
		Token token=new Token();
		token.setUserid(id);
		token.setValue(tok);
		System.out.println("token: "+tok);
		if(!tokenDAO.verifyToken(token))
			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
		
		System.out.println("Verification successfull");

        if (file.isEmpty()) new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        try
        {
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
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @RequestMapping(value= {"/upload/cpp/{token}","/upload/cpp/{token}/{macaddress}"},method=RequestMethod.POST) 
    public ResponseEntity singleFileUploadcpp(@RequestParam("file") MultipartFile file,@PathVariable(name="token") String tok,@PathVariable(name="macaddress",required = false) String macaddress,HttpServletRequest request) throws InterruptedException{
    
    	int id=0;
    	if(macaddress==null) 
    	{
    		String ipAddress =  request.getRemoteAddr();
   		id=userDAO.addorgetUser(ipAddress);
		}
    	else 
    	{
   		id=userDAO.addorgetUser(macaddress);
    	}
	Token token=new Token();
	token.setUserid(id);
	token.setValue(tok);
	//System.out.println("token CPP: "+tok);
	if(!tokenDAO.verifyToken(token))
		return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

	//System.out.println("Verification successfull for CPP");
        if (file.isEmpty()) return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);

        try
        {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOAD_FOLDER_CPP+file.getOriginalFilename());
	    //System.out.println(file.getOriginalFilename());
            Files.write(path, bytes);

	    // decrypt the file
	    String command = "/usr/tomcat/cppfiles/decrypt "+path.toString();
	    //System.out.println(command);
	    Process process = Runtime.getRuntime().exec(command);
	    int exitValue = process.waitFor();
	    if (exitValue != 0) {
		    System.out.println("Abnormal process termination for CPP file decryption");
	    }

	    // move the file to add to the database
	    String command1 = "mv "+path.toString()+" "+DECRYPTED_FILES_DIR;
            Process process1 = Runtime.getRuntime().exec(command1);
	    //System.out.println(command1);

        }
        catch (IOException e) 
        {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
   
    @RequestMapping(value= {"/movefile/{fname}"},method=RequestMethod.GET) 
    public String movefile(@PathVariable(name="fname") String fname, HttpServletRequest request) {
        try
        {
            // Get the file and save it somewhere
	    System.out.println(fname);
            Path path = Paths.get(UPLOAD_FOLDER_CPP+fname);
	    //String command = "/usr/tomcat/cppfiles/decrypt "+path.toString()+ " && sleep 5 && cp "+path.toString()+" "+DECRYPTED_FILES_DIR+" && rm -f "+path.toString();
	    String command = "mv "+path.toString()+" "+Paths.get(DECRYPTED_FILES_DIR+fname).toString();
	    System.out.println(command);
	    Runtime.getRuntime().exec(command);
        }
        catch (IOException e) 
        {
            e.printStackTrace();
        //    return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return "home";
    }

	@RequestMapping(value="/verifyToken/{token}", method=RequestMethod.GET)
	@ResponseBody
	public String verifyToken(@PathVariable(name="token") String tok, HttpServletRequest request){		
		String ipAddress =  request.getRemoteAddr();
		int id=userDAO.addorgetUser(ipAddress);
		Token token=new Token();
		token.setUserid(id);
		token.setValue(tok);
		if(tokenDAO.verifyToken(token))
			return tok;
		return tokenDAO.assignToken(id);
	}

	
}
