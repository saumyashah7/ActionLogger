package com.java.logspringmvc.controller;

import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
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

import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

@Controller
public class HomeController {
	
	private static String UPLOADED_FOLDER = "E://UTSA//json//";

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
	
	public void parseJsonFiles(String dir) throws IOException, ParseException, CryptoException
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
	
	@RequestMapping(value= {"/","/home"})
	public String listLogs(Model mod) throws IOException, ParseException, CryptoException {
		//dc.decryptandaddLog("E:\\Spring\\logspringmvc\\action_logs.txt");
		//dc.decryptMAClogfile("E:\\UTSA\\json\\actions_00-15-5D-33-C1-5A.json");		
		List<Log> listlogs=logDAO.getLogs(); 
		mod.addAttribute("listLogs", listlogs);
		return "home";
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
	
	@RequestMapping(value= {"/appusage"})
	public String listUsage(Model mod) throws IOException, ParseException, CryptoException {
		parseJsonFiles("E:\\UTSA\\json");
		List<UsageMetric> usagelist = usagemetricDAO.getAppUsage();  
		mod.addAttribute("usagelist", usagelist);
		return "appusage";
	}
	
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
//	@RequestMapping(value= {"/upload/{token}","/upload/{token}/{macaddress}"},method=RequestMethod.POST) 
    @RequestMapping(value= "/upload/{token}/{macaddress}",method=RequestMethod.POST) 
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
            Path path = Paths.get(UPLOADED_FOLDER+file.getOriginalFilename());
            Files.write(path, bytes);
        }
        catch (IOException e) 
        {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(HttpStatus.OK);
    }
    
    @RequestMapping(value= "/upload/cpp/{token}/{macaddress}",method=RequestMethod.POST) 
    public ResponseEntity singleFileUploadcpp(@RequestParam("file") MultipartFile file,@PathVariable(name="token") String tok,@PathVariable(name="macaddress") String macaddress,HttpServletRequest request) {
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
//		Token token=new Token();
//		token.setUserid(id);
//		token.setValue(tok);
//		System.out.println("token: "+tok);
//		if(!tokenDAO.verifyToken(token))
//			return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
//		
//		System.out.println("Verification successfull");

        if (file.isEmpty()) new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        try
        {
            // Get the file and save it somewhere
            byte[] bytes = file.getBytes();
            Path path = Paths.get(UPLOADED_FOLDER+file.getOriginalFilename());
            Files.write(path, bytes);
        }
        catch (IOException e) 
        {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.NOT_ACCEPTABLE);
        }
        return new ResponseEntity<>(HttpStatus.OK);
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
