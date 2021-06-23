package com.java.logspringmvc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Scanner;

import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.java.logspringmvc.dao.UsageMetricDAO;
import com.java.logspringmvc.dao.UserDAO;
import com.java.logspringmvc.model.UsageMetric;
import com.java.logspringmvc.model.User;

public class JsonUtils {

	@Autowired
	private UserDAO userDAO;

	@Autowired
	private UsageMetricDAO usagemetricDAO;

	@SuppressWarnings("rawtypes")
	public void logJsonFile(String filename) throws IOException, ParseException
	{
		JSONParser parser=new JSONParser();
		Object obj=parser.parse(new FileReader(filename));
		JSONObject json=(JSONObject)obj;

		String ipjson = filename.split("_")[1];
		String ip = ipjson.substring(0,ipjson.lastIndexOf("."));
		int id=userDAO.addorgetUser(ip);

		String app=null;
		HashMap<String,Integer> metrics=new HashMap<>();

		for(Iterator iterator = json.keySet().iterator(); iterator.hasNext();)
		{
		    String key = (String) iterator.next();
		    if(key.equals("software-name".toString()))
		    {
		    	app=json.get(key).toString();
		    	continue;
		    }
		    if(key.equals("EAGERlastsentdatetime".toString()))
		    	continue;
		    metrics.put(key.toString(), Integer.parseInt(json.get(key).toString()));
		}

		UsageMetric um;
		for(String key:metrics.keySet())
		{
			um=new UsageMetric();
			um.setUserid(id);
			um.setApplication(app);
			um.setMetric(key);
			um.setUsage(metrics.get(key));
			usagemetricDAO.updateUsage(um);
		}

	}
}
