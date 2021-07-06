package com.utsa.eager.utils;



import java.io.FileReader;
import java.io.IOException;
import java.util.HashMap;
import java.util.Iterator;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.utsa.eager.model.UsageMetric;
import com.utsa.eager.service.UsageMetricService;
import com.utsa.eager.service.UserService;

@Component
public class JsonUtils {

	@Autowired
	private UserService userService;

	@Autowired
	private UsageMetricService usagemetricService;

	@SuppressWarnings("rawtypes")
	public void logJsonFile(String filename) throws IOException, ParseException
	{
		JSONParser parser=new JSONParser();
		Object obj=parser.parse(new FileReader(filename));
		JSONObject json=(JSONObject)obj;

		String ipjson = filename.split("_")[1];
		String ip = ipjson.substring(0,ipjson.lastIndexOf("."));
		int id=userService.addorgetUser(ip);

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
			usagemetricService.updateUsage(um);
		}

	}
}