/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socketTest;

import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.InetAddress;
import java.net.Socket;

/**
 *
 * @author mamishev.d.a
 */
public class Client {

    public static void main(String[] args) throws IOException {
        int port = 6666;
        String serverAddress = "10.36.201.68";
        InetAddress server = InetAddress.getByName(serverAddress);
        Socket socket = new Socket(server, port);
        InputStream sin = socket.getInputStream();
        OutputStream sout = socket.getOutputStream();
        DataInputStream dataIn = new DataInputStream(sin);
        DataOutputStream dataOut = new DataOutputStream(sout);
        
        BufferedReader keyboard = new BufferedReader(new InputStreamReader(System.in));
        
        
        String line = null;
        while (true) {
            line = keyboard.readLine();
            dataOut.writeUTF(line);
            dataOut.flush();
            line = dataIn.readUTF();
            System.out.println("from serv ---- begin");
            System.out.println(line);
            System.out.println("from serv ---- end");
        }

    }
}
