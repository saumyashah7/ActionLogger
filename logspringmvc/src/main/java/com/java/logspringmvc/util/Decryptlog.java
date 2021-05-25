package com.java.logspringmvc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.beans.factory.annotation.Autowired;

import com.java.logspringmvc.dao.LogDAO;
import com.java.logspringmvc.dao.UsageMetricDAO;
import com.java.logspringmvc.dao.UserDAO;
import com.java.logspringmvc.model.Log;
import com.java.logspringmvc.model.UsageMetric;
import com.java.logspringmvc.model.User;


public class Decryptlog {
	private final String strkey = "1122334455667788";
	private final String ALGORITHM = "AES";
    private final String TRANSFORMATION = "AES";
    
	@Autowired
	private LogDAO logDAO;
	
	@Autowired
	private UserDAO userDAO;
	
	@Autowired
	private UsageMetricDAO usagemetricDAO;
    
    private String decrypt(String key, String input)
            throws CryptoException {
        return doCrypto(Cipher.DECRYPT_MODE, key, input);
    }
 
    private String doCrypto(int cipherMode, String key, String input) throws CryptoException {
        try {

        	SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), ALGORITHM);
            Cipher cipher = Cipher.getInstance(TRANSFORMATION);
            cipher.init(cipherMode, secretKey);
            byte[] outputBytes = null;
            if(cipherMode==1)
            {	
            	outputBytes = cipher.doFinal(input.getBytes("UTF-8"));
            	return new String(Base64.encodeBase64(outputBytes));
            }
            if(cipherMode==2)
            	outputBytes = cipher.doFinal(Base64.decodeBase64(input));
            return new String(outputBytes);
             
        } catch (NoSuchPaddingException | NoSuchAlgorithmException
                | InvalidKeyException | BadPaddingException
                | IllegalBlockSizeException | IOException ex) {
            throw new CryptoException("Error encrypting/decrypting file", ex);
        }
    }
    
	public static byte[] getBytesstr(String bytestring) 
	{
		int cnt=0;
		System.out.println(bytestring);
    	for(char c:bytestring.toCharArray()) 
    	{
    		if(c==',')cnt++;
    	}
    	byte[] bytes=new byte[cnt+1];
    	int i=0;
    	for(String s:bytestring.substring(1, bytestring.length()-1).split(",")) 
    	{    		
    		bytes[i++]=Byte.valueOf(s.trim());
    	}
    	return bytes;
		
	}
	
	public void decryptLog(String filename) throws CryptoException, FileNotFoundException 
	{
		File infile = new File(filename);
		File file = new File(filename.split("\\.")[0]+"_decrypted.txt");
		FileWriter writer=null;
		Scanner sc=new Scanner(infile);
		while(sc.hasNextLine()) {
		String msg=sc.nextLine();
        try 
        {
        	if (file.createNewFile()) 
            {
            	writer = new FileWriter(file);
            	String bytestring= new String(decrypt(strkey,msg));
            	writer.write(new String(getBytesstr(bytestring)));
            }
            else 
            {
            	writer = new FileWriter(file, true);
            	String bytestring= new String(decrypt(strkey,msg));
            	writer.write("\n" + new String(getBytesstr(bytestring)));
            }
            writer.close();
        } 
        catch (IOException e) 
        {
        	e.printStackTrace();
        }
		}
        
	}
	
	@SuppressWarnings("rawtypes")
	public void decryptMAClogfile(String filename) throws IOException, ParseException, CryptoException
	{
		JSONParser parser=new JSONParser();
		Object obj=parser.parse(new FileReader(filename));
		JSONObject json=(JSONObject)obj;
	
		String mac=filename.split("\\.")[0].split("_")[1];
		int id=userDAO.addUser(mac);
		
		
		String app=null;
		HashMap<String,Integer> metrics=new HashMap<>();
		
		for(Iterator iterator = json.keySet().iterator(); iterator.hasNext();) 
		{
		    String key = (String) iterator.next();
		    if(decrypt(strkey,key).equals("software-name".toString())) 
		    {
		    	app=decrypt(strkey,json.get(key).toString());
		    	continue;
		    }
		    if(decrypt(strkey,key).equals("EAGERlastsentdatetime".toString())) 
		    	continue;
		    metrics.put(decrypt(strkey,key.toString()), Integer.parseInt(decrypt(strkey,json.get(key).toString())));
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
	
	public void decryptandaddLog(String filename) throws CryptoException, FileNotFoundException 
	{
		File infile = new File(filename);
		Scanner sc=new Scanner(infile);
		while(sc.hasNextLine()) {
		String bytestring= new String(decrypt(strkey,sc.nextLine()));	
		String line=new String(getBytesstr(bytestring));
		String[] cols=line.split(",");
		Log log=new Log(cols[0].trim(),cols[1].trim(),cols[2].trim(),cols[3].trim());
		logDAO.addlog(log);
		}
        
	}
}
