package wrapAsService;


import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.Socket;

public class ExecuteCommandClient
{
	public static void main(String[] args) throws Exception
	{
		Socket aSocket = new Socket("127.0.0.1", ExecuteRDPCommandServer.PORT);
		
		PrintWriter out = new PrintWriter(aSocket.getOutputStream(), true);
		
		BufferedReader in = new BufferedReader(
		        new InputStreamReader(aSocket.getInputStream()));
		
		String aLine = null;
		
		out.write("-q\n");
		out.write("/data/seqs/806rcbc00_AN34_2.fasta\n");
		
		out.write("-o\n");
		out.write("/data/seqs/806rcbc00_AN34_2.rdp\n");
		out.write("GO\n");
		out.flush();
		
		//while( aSocket.isConnected())
		{
			 while ((aLine= in.readLine()) != null) {
				 System.out.println(aLine);		    
				}
			 
			 Thread.sleep(1000);
		}
		
		out.flush();  out.close(); aSocket.close();
	}
}
