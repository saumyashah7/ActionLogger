package com.java.logspringmvc.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;

import com.java.logspringmvc.dao.LogDAO;
import com.java.logspringmvc.model.Log;
import com.java.logspringmvc.util.CryptoException;
import com.java.logspringmvc.util.Decryptlog;

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
	public String addLogpost(@RequestParam("datetime") String time, @RequestParam("application") String application, @RequestParam("method") String method, @RequestParam("description") String description){
		logDAO.addlog(new Log(time,application,method,description));
		return "redirect:/home";
	}
	
	
}
