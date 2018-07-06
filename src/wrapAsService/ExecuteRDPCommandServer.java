package wrapAsService;


import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

// something like 
// docker run -it rdptest:latest /bin/bash 
// docker run -p 3491:3491 rdptest6:latest /root/startServer.bat
//
// where .bat file is something like
// #!/bin/bash
//  usr/bin/java -cp /root/git/dockerpush/bin wrapAsService.ExecuteRDPCommandServer

public class ExecuteRDPCommandServer
{
	public static final int PORT = 3491;
	
	public static void main(String[] args) throws Exception
	{
		ServerSocket sSocket = new ServerSocket(PORT);
		System.out.println("listening at " + PORT);
		
		while(true)
		{
			Socket aSocket =  sSocket.accept();
			System.out.println("Got request" + aSocket.getPort());
			
			List<String> cmdList = new ArrayList<>();
			cmdList.add("/usr/bin/java");
			cmdList.add("-jar");
			cmdList.add("/apps/rdp_classifier_2.12/dist/classifier.jar");
			
			BufferedReader in = new BufferedReader(
			        new InputStreamReader(aSocket.getInputStream()));
			
			BufferedWriter writer = new BufferedWriter(
					new OutputStreamWriter(aSocket.getOutputStream()));
			
			boolean spin= true;
			boolean go = false;
			
			 while (spin)
			 {
				 String aLine = in.readLine();
				 
				 if( aLine == null)
				 {
					 spin = false;
				 }
				 else if( aLine.equals("GO"))
				 {
					 spin = false; 
					 go = true;
					 System.out.println("Starting");
				 }
				 else
				 {
					 cmdList.add(aLine);
					 System.out.println("Got " + aLine);
				 }
			 }
			 
			 String[] cmdArgs = new String[cmdList.size()];
			 
			 StringBuffer buff = new StringBuffer();
			 
			 for( int x=0; x < cmdArgs.length; x++)
				 buff.append(cmdArgs[x] + " " );
			 
			 System.out.println(cmdArgs);
			
			 if( go)
			 {
				 try
				 {
					 for( int x=0; x < cmdArgs.length; x++)
						 cmdArgs[x] = cmdList.get(x); 
					 
					 Runtime r = Runtime.getRuntime();
					 Process p = r.exec(cmdArgs);

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
					 ex.printStackTrace();
					 writer.write(ex.getMessage());
				 }
			 }
				
				 System.out.println("Closing");
				 in.close();
				 try
				 {
					 writer.flush();
				 }
				 catch(Exception ex)
				 {
					System.out.println("Warining failed to flush writer"); 
				 }
				 
				 try
				 {
					 writer.close();
				 }
				 catch(Exception ex)
				 {

						System.out.println("Warining failed to close writer"); 
				 }
				 
				 try
				 {
					 aSocket.close();
				 }
				 catch(Exception ex)
				 {
					 System.out.println("Socket already closed"); 
				 }
		}
	}
}
