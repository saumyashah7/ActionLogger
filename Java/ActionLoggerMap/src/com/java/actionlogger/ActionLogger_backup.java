package com.java.actionlogger;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;  // Import the IOException class to handle errors
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.sql.Timestamp;
import java.util.Iterator;
import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;
import org.apache.commons.codec.binary.Base64;
import org.apache.http.HttpEntity;
import org.apache.http.client.methods.CloseableHttpResponse;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.ContentType;
import org.apache.http.entity.mime.MultipartEntityBuilder;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClients;
import org.apache.http.util.EntityUtils;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.java.actionloggerexception.CryptoException;


public class ActionLogger_backup 
{	
	private final static String ALGORITHM = "AES";
    private final static String TRANSFORMATION = "AES";
    //private static final String strkey = "1239567890123456";
    private final static String strkey = "1122334455667788";
    private final static String UPLOAD_URL = "http://129.114.104.163:8080/upload/java";
    private final static String TOKEN_URL = "http://129.114.104.163:8080/getToken";
    //private final static String UPLOAD_URL = "https://eagerapp1.herokuapp.com/upload/java";
    //private final static String TOKEN_URL = "https://eagerapp1.herokuapp.com/getToken";
	
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public static void decryptLog(String filename) throws FileNotFoundException, IOException, ParseException, CryptoException 
	{
		
		JSONParser parser=new JSONParser();
		Object obj=parser.parse(new FileReader(filename));
		JSONObject json=(JSONObject)obj;
		String mac=getMAC();
		File yourFile = new File("actions_"+mac+"_decrypted.json");
		FileWriter writer=new FileWriter("actions_"+mac+"_decrypted.json");;
		yourFile.createNewFile(); //creating it
	    JSONObject js=new JSONObject();
	   
		
		for(Iterator iterator = json.keySet().iterator(); iterator.hasNext();) {
		    String key = (String) iterator.next();
		    js.put(decrypt(strkey,key), decrypt(strkey,json.get(key).toString()));		    
		    
		}
		writer.write(js.toJSONString());
		writer.close();
		        
	}
		
    public static String encrypt(String key, String input)
            throws CryptoException {
        return doCrypto(Cipher.ENCRYPT_MODE, key, input);
    }
  
    public static String decrypt(String key, String input)
            throws CryptoException {
        return doCrypto(Cipher.DECRYPT_MODE, key, input);
    }
 
    private static String doCrypto(int cipherMode, String key, String input) throws CryptoException {
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

    public static String getMAC() throws UnknownHostException, SocketException {
    	InetAddress localHost = InetAddress.getLocalHost();
    	NetworkInterface ni = NetworkInterface.getByInetAddress(localHost);
    	byte[] hardwareAddress = ni.getHardwareAddress();
    	String[] hexadecimal = new String[hardwareAddress.length];
    	for (int i = 0; i < hardwareAddress.length; i++) {
    	    hexadecimal[i] = String.format("%02X", hardwareAddress[i]);
    	}
    	String macAddress = String.join("-", hexadecimal);
    	return macAddress;
    }
    
	private static void sendPOST(String filename) throws IOException {

		String token=sendTokenGET();
		
    	CloseableHttpClient httpClient = HttpClients.createDefault();
    	HttpPost uploadFile = new HttpPost(UPLOAD_URL+"/"+token+"/"+getMAC());
    	MultipartEntityBuilder builder = MultipartEntityBuilder.create();
    	

    	// This attaches the file to the POST:
    	File f = new File(filename);
    	builder.addBinaryBody("file", new FileInputStream(f), ContentType.APPLICATION_OCTET_STREAM, f.getName());

    	HttpEntity multipart = builder.build();
    	uploadFile.setEntity(multipart);
    	CloseableHttpResponse response = httpClient.execute(uploadFile);
    	//HttpEntity responseEntity = response.getEntity();
    	
	}
	
	private static String sendTokenGET() throws IOException {
    	CloseableHttpClient httpClient = HttpClients.createDefault();
    	HttpGet getToken = new HttpGet(TOKEN_URL+"/"+getMAC());
    	CloseableHttpResponse response = httpClient.execute(getToken);
    	return EntityUtils.toString(response.getEntity());
	}
    
    public static boolean checkTimestamp(String timeStamp) throws UnknownHostException, SocketException, IOException {
    	
    	Timestamp curtime= new Timestamp(System.currentTimeMillis());
    	Timestamp pasttime=Timestamp.valueOf(timeStamp);
    	long milliseconds1 = pasttime.getTime();
    	long milliseconds2 = curtime.getTime();
    	long diff = milliseconds2 - milliseconds1;
    	
    	long diffSeconds = diff / 1000;
    	long diffMinutes = diff / (60 * 1000);
    	long diffHours = diff / (60 * 60 * 1000);
    	long diffDays = diff / (24 * 60 * 60 * 1000);
    	System.out.println("diffMinute: "+diffMinutes);
    	if(diffMinutes>0) {
    		  sendPOST("actions_"+getMAC()+".json");
    		  return true;
    	}
    	  return false;
 
    }
    
    @SuppressWarnings("unchecked")
	public static void log(String msg) throws IOException, ParseException, CryptoException 
    {
    	if(msg.split(",").length==1) 
    	{
    		String app =  msg;
    		String encrypted_app = encrypt(strkey, "usage");
    		String mac=getMAC();
    		File jsonFile = new File("actions_"+mac+".json");
    		FileWriter writer=null;

    		if(!jsonFile.exists()) 
    		{
    			jsonFile.createNewFile(); //creating it
    		    
    		    JSONObject js=new JSONObject();
    		    js.put(encrypt(strkey,"software-name"), encrypt(strkey,app));
    		    js.put(encrypted_app, encrypt(strkey,"1"));
    		    js.put(encrypt(strkey,"EAGERlastsentdatetime"), encrypt(strkey,new Timestamp(System.currentTimeMillis()).toString()));
    		    
    		    writer=new FileWriter("actions_"+mac+".json");
    		    writer.write(js.toJSONString());
    		    writer.close();
    		    return;
    		}

    		
    		JSONParser parser=new JSONParser();
    		Object obj=parser.parse(new FileReader("actions_"+mac+".json"));
    		JSONObject json=(JSONObject)obj;
    		
    		if(json.containsKey(encrypted_app)) 
    		{
    			int cnt=Integer.parseInt(decrypt(strkey, json.get(encrypted_app).toString()));
    			++cnt;
    			json.put(encrypted_app, encrypt(strkey,String.valueOf(cnt)));
    			writer=new FileWriter("actions_"+mac+".json");
    			writer.write(json.toJSONString());
    			writer.close();
    		}
    		
    		if(checkTimestamp(decrypt(strkey, json.get(encrypt(strkey,"EAGERlastsentdatetime")).toString()))) {
    			System.out.println("Updating timestamp");
    			json.put(encrypt(strkey,"EAGERlastsentdatetime"),encrypt(strkey,new Timestamp(System.currentTimeMillis()).toString()));
    		}
    		
    		writer=new FileWriter("actions_"+mac+".json");
    		writer.write(json.toJSONString());
    		writer.close();
    	}
    	else 
    	{
			String app =  msg.split(",")[0];
			String function =  msg.split(",")[1];
			String encrypted_app = encrypt(strkey, "usage");
			String encrypted_function = encrypt(strkey, function);
			String mac=getMAC();
			File jsonFile = new File("actions_"+mac+".json");
			FileWriter writer=null;
	
			if(!jsonFile.exists()) 
			{
				jsonFile.createNewFile(); //creating it
			    
			    JSONObject js=new JSONObject();
			    js.put(encrypt(strkey,"software-name"), encrypt(strkey,app));
			    js.put(encrypted_app, encrypt(strkey,"1"));
			    js.put(encrypted_function, encrypt(strkey,"1"));
			    js.put(encrypt(strkey,"EAGERlastsentdatetime"), encrypt(strkey,new Timestamp(System.currentTimeMillis()).toString()));
			    
			    writer=new FileWriter("actions_"+mac+".json");
			    writer.write(js.toJSONString());
			    writer.close();
			    return;
			}
	
			JSONParser parser=new JSONParser();
			Object obj=parser.parse(new FileReader("actions_"+mac+".json"));
			JSONObject json=(JSONObject)obj;
			
			if(json.containsKey(encrypted_app)) 
			{
				int cnt=Integer.parseInt(decrypt(strkey, json.get(encrypted_app).toString()));
				++cnt;
				json.put(encrypted_app, encrypt(strkey,String.valueOf(cnt)));
				writer=new FileWriter("actions_"+mac+".json");
				writer.write(json.toJSONString());
				writer.close();
			}		
			
			if(json.containsKey(encrypted_function)) 
			{
				int cnt=Integer.parseInt(decrypt(strkey,json.get(encrypted_function).toString()));
				++cnt;
				json.put(encrypted_function, encrypt(strkey,String.valueOf(cnt)));
			}
			else
			{
				json.put(encrypted_function, encrypt(strkey,"1"));
			}
			
			if(checkTimestamp(decrypt(strkey, json.get(encrypt(strkey,"EAGERlastsentdatetime")).toString()))) {
				System.out.println("Updating timestamp");
				json.put(encrypt(strkey,"EAGERlastsentdatetime"),encrypt(strkey,new Timestamp(System.currentTimeMillis()).toString()));
			}
			
			writer=new FileWriter("actions_"+mac+".json");
			writer.write(json.toJSONString());
			writer.close();
    	}
		
    }

	public static void main(String[] args) throws CryptoException, NoSuchAlgorithmException, ParseException, IOException {
//		log("ReActionlogger,main method");
		log("ReActionlogger123,met1");
		log("ReActionlogger123,met2");
		log("ReActionlogger123,met3");
		log("ReActionlogger123,met4");
//		log("ReActionlogger,main method ");
//		log("ReActionlogger,login method ");
//		log("ReActionlogger,login method ");
//		log("ReActionlogger,messageboards method ");
//		log("ReActionlogger,messageboards method ");
//		log("ReActionlogger,messageboards method ");
//		log("ReActionlogger,messageboards method ");
		decryptLog("actions_"+getMAC()+".json");
		//System.out.println(sendTokenGET());

	}
}
