package com.java.logspringmvc.controller;

import java.io.IOException;
import java.util.List;
import java.util.Map;

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
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.servlet.ModelAndView;

import com.java.logspringmvc.dao.LogDAO;
import com.java.logspringmvc.model.Log;
import com.java.logspringmvc.util.CryptoException;
import com.java.logspringmvc.util.Decryptlog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

@Controller
public class HomeController {

	@Autowired
	private LogDAO logDAO;
	
	@Autowired
	private Decryptlog dc;
	
	
	
	@RequestMapping(value= {"/","/home"})
	public String listLogs(Model mod) throws IOException, CryptoException{
		//dc.decryptandaddLog("E:\\Spring\\logspringmvc\\action_logs.txt");
		List<Log> listlogs=logDAO.getLogs(); 
		mod.addAttribute("listLogs", listlogs);
		return "home";
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
		final JSONObject obj = new JSONObject(payload);
		logDAO.addlog(new Log(obj.getString("datetime"),obj.getString("application"),obj.getString("method"),obj.getString("description")));
		
	}
	
	
}
