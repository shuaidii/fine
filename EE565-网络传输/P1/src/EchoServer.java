import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;


public class EchoServer 
{
    public static void main(String[] args) throws IOException
   { 
    ServerSocket serverSocket = null;
    int PortNumber=5;
    if (args.length > 0){
        PortNumber = Integer.parseInt(args[0]);
     }
     else{
         PortNumber=8000;
//        PortNumber = 10007;
     }
    try { 
         serverSocket = new ServerSocket(PortNumber);
        } 
    catch (IOException e) 
        { 
         System.err.println("Could not listen on port: 8000.");

         System.exit(1); 
        } 

    
    System.out.println ("Waiting for connection.....");

    try { 
            while(true){
                Socket clientSocket = serverSocket.accept(); 
                EchoHandler handler=new EchoHandler(clientSocket);
                handler.start();
            }
        } 
    catch (IOException e) 
        { 
         System.err.println("Accept failed."); 
         System.exit(1); 
        } 

    serverSocket.close(); 
   } 
} 