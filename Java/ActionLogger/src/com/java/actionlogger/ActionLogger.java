package com.java.actionlogger;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;  // Import the IOException class to handle errors


public class ActionLogger 
{	
	public static void log(String msg) 
	{
		File file = new File("action_logs.txt");
		FileWriter writer=null;
        try 
        {
            if (file.createNewFile()) 
            {
            	writer = new FileWriter(file);
            	writer.write(msg);
            }
            else 
            {
            	writer = new FileWriter(file, true);
            	writer.write("\n" + msg);
            }
            writer.close();
        } 
        catch (IOException e) 
        {
            e.printStackTrace();
        }
	}

}
