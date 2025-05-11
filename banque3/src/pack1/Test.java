package pack1;

import java.io.IOException;
import java.io.PrintWriter;
import java.net.Socket;
import java.net.UnknownHostException;

public class Test {
    
    public static void main (String [] args ) throws UnknownHostException, IOException {
        
        System.out.println("C'est bon");
        String serveurIP = "192.168.164.254";
        int port = 5000;
        
        Socket socket = new Socket( serveurIP, port);
        PrintWriter out = new PrintWriter(socket.getOutputStream() , true );
        
        out.println(1);
        
        out.close();
        socket.close();

        System.out.println("Message envoyé.");

    }
}