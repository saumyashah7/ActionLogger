package com.java.logspringmvc.controller;

import java.io.IOException;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

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
	
	@RequestMapping(value="/")
	public ModelAndView listLogs(ModelAndView mod) throws IOException, CryptoException{
		dc.decryptandaddLog("E:\\Spring\\logspringmvc\\action_logs.txt");
		List<Log> listlogs=logDAO.getLogs(); 
		mod.addObject("listLogs", listlogs);
		mod.setViewName("home");
		return mod;
	}
}
