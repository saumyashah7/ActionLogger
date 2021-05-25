package com.java.logspringmvc.controller;

import java.io.File;
import java.io.IOException;
import java.nio.file.DirectoryStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.stream.Stream;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.commons.CommonsMultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.java.logspringmvc.dao.LogDAO;
import com.java.logspringmvc.dao.UsageMetricDAO;
import com.java.logspringmvc.model.Log;
import com.java.logspringmvc.model.UsageMetric;
import com.java.logspringmvc.util.CryptoException;
import com.java.logspringmvc.util.Decryptlog;

import org.apache.tomcat.util.http.fileupload.FileUtils;
import org.json.JSONException;
import org.json.JSONObject;
import org.json.simple.parser.ParseException;

@Controller
public class HomeController {
	
	private static String UPLOADED_FOLDER = "E://UTSA//json//";

	@Autowired
	private LogDAO logDAO;
	
	@Autowired
	UsageMetricDAO usermetricDAO;
	
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
	
	@RequestMapping(value= {"/appusage"})
	public String listUsage(Model mod) throws IOException, ParseException, CryptoException {
		parseJsonFiles("E:\\UTSA\\json");
		List<UsageMetric> usagelist = usermetricDAO.getAppUsage();  
		mod.addAttribute("usagelist", usagelist);
		return "appusage";
	}	
	
	@RequestMapping(value="/addlog/{datetime}/{application}/{method}/{description}")
	public String addLog(@PathVariable("datetime") String time, @PathVariable("application") String application, @PathVariable("method") String method, @PathVariable("description") String description){
		logDAO.addlog(new Log(time,application,method,description));
		return "redirect:/home";
	}
	
	@RequestMapping(value="/addlog", method=RequestMethod.POST)
	public void addLogpost(@RequestBody String payload) throws JSONException {
	//public void addLogpost(@RequestParam("datetime") String datetime, @RequestParam("application") String application, @RequestParam("method") String method, @RequestParam("description") String description){		
		//logDAO.addlog(new Log(datetime,application,method,description));
		
		//from weburl post request with JSON payload
		//final JSONObject obj = new JSONObject(payload);
		//logDAO.addlog(new Log(obj.getString("datetime"),obj.getString("application"),obj.getString("method"),obj.getString("description")));
		
	}
	
    @RequestMapping(value="/upload",method=RequestMethod.POST) // //new annotation since 4.3
    public void singleFileUpload(@RequestParam("file") MultipartFile file) {

        if (file.isEmpty()) return ;
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
        }
    }	
}
