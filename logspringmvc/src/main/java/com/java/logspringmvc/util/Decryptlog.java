package com.java.logspringmvc.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.Scanner;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.springframework.beans.factory.annotation.Autowired;

import com.java.logspringmvc.dao.LogDAO;
import com.java.logspringmvc.model.Log;


public class Decryptlog {
	private final String strkey = "1234567890123456";
	private final String ALGORITHM = "AES";
    private final String TRANSFORMATION = "AES";
    
	@Autowired
	private LogDAO logDAO;
    
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
            	writer.write(decrypt(strkey,msg));
            }
            else 
            {
            	writer = new FileWriter(file, true);
            	writer.write("\n" + decrypt(strkey,msg));
            }
            writer.close();
        } 
        catch (IOException e) 
        {
        	e.printStackTrace();
        }
		}
        
	}
	public void decryptandaddLog(String filename) throws CryptoException, FileNotFoundException 
	{
		File infile = new File(filename);
		Scanner sc=new Scanner(infile);
		while(sc.hasNextLine()) {
		String line=decrypt(strkey,sc.nextLine());
		String[] cols=line.split(",");
		Log log=new Log(cols[0].trim(),cols[1].trim(),cols[2].trim(),cols[3].trim());
		logDAO.addlog(log);
		}
        
	}
}
