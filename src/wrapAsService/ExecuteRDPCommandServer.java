package wrapAsService;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import utils.ProcessWrapper;

public class ExecuteRDPCommandServer
{
	public static final int PORT = 3491;
	
	public static void main(String[] args) throws Exception
	{
		ServerSocket sSocket = new ServerSocket(PORT);
		
		while(true)
		{
			Socket aSocket =  sSocket.accept();
			
			List<String> cmdList = new ArrayList<>();
			cmdList.add("/usr/bin/java");
			cmdList.add("-jar");
			cmdList.add("/apps/rdp_classifier_2.12/dist/classifier.jar");
			
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(aSocket.getInputStream()));
			
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(aSocket.getOutputStream()));
			
			String aLine = null;
			
			 while ((aLine= in.readLine()) != null) {
				 cmdList.add(aLine);
			    }
			 
			 String[] cmdArgs = new String[cmdList.size()];
			 
			 for( int x=0; x < cmdArgs.length; x++)
				 cmdArgs[x] = cmdList.get(x); 
			 
			 Runtime r = Runtime.getRuntime();
			 Process p = r.exec(cmdArgs);
				
			 
			 try
			 {
				 BufferedReader br = new BufferedReader (new InputStreamReader(p.getInputStream ()));
					
					String s;
					
					while ((s = br.readLine ())!= null)
					{
			    		System.out.println (s);
			    		writer.write(s);
					}
							
					p.waitFor();
					p.destroy();
			 }
			 catch(Exception ex)
			 {
				 writer.write(ex.toString());
			 }
			 finally
			 {
				 System.out.println("Closing");
				 in.close();
				 writer.flush();
				 writer.close();
				 aSocket.close();
			 }	
		}
	}
}
