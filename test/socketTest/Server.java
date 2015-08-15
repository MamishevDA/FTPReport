/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package socketTest;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.print.DocFlavor;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author mamishev.d.a
 */
public class Server {

    public Server() {
        initAndRun();
    }
    private int port = 0;
    private ServerSocket serv = null;
    private Socket socket = null;
    private InputStream sin = null;
    private OutputStream sout = null;
    private DataInputStream dataIn = null;
    private DataOutputStream dataOut = null;

    private void initAndRun() {
        try {
            port = 6666;
            serv = new ServerSocket(port);
            System.out.println("ждем клиента");
            socket = serv.accept();
            System.out.println("есть клиент!");
            sin = socket.getInputStream();
            sout = socket.getOutputStream();
            dataIn = new DataInputStream(sin);
            dataOut = new DataOutputStream(sout);

            String pathFile = null;
            while (true) {
                //подразумевается, что придет именно путь и имя файла
                pathFile = dataIn.readUTF();
                System.out.println("server get the line " + pathFile);
                //String theLog = getLog(pathFile);
                String theLog = "get log";
                dataOut.writeUTF(theLog);
                dataOut.flush();
            }


        } catch (IOException ex) {
            initAndRun();
        } finally {
            try {
                dataIn.close();
                dataOut.close();
                sin.close();
                sout.close();
                socket.close();
                serv.close();
            } catch (IOException ex) {
                Logger.getLogger(Server.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }

    private String getLog(String pathLog) {
        int fromPosition = 0;

        Pattern regex = Pattern.compile("-{50,}");
        //String pathLog = "\\\\10.77.2.21\\pos_client\\log\\s" + aptName + ".log";
        //String pathLog = "\\\\10.77.2.21\\pos_client\\log\\r" + ".log";
        File f = new File(pathLog);
        String theLog = "the Log of" + pathLog + "\n";
        if (f.exists()) {
            try {
                theLog = theLog + "Размер полного лога " + String.valueOf(f.length() / 1024 / 1024) + "МБ \n";
                theLog.concat(String.valueOf(f.length()) + "\n");
                //System.out.printf("the file is %1$s \n", pathLog);
                List<String> l = FileUtils.readLines(f);
                String[] allLines = l.toArray(new String[0]);
                for (int i = allLines.length - 1; i >= 0; i--) {
                    Matcher m = regex.matcher(allLines[i]);
                    //найдем позицию с которой тянуть лог, можкт учитывать ошибки ORA?
                    if (m.find() && fromPosition == 0) {
                        fromPosition = i;
                        System.out.printf("%1$s from line  %2$s \n", allLines[i], fromPosition);
                    }
                }
                String sLog[] = l.subList(fromPosition, l.size()).toArray(new String[0]);
                for (String s : sLog) {
                    theLog = theLog + s + "\n";
                }
                System.out.printf("the Log %1$s \n", pathLog);
            } catch (IOException ex) {
                System.out.printf("error in makeLog %1$s \n", ex);
            }
        } else {
            System.out.printf("Can't found log %1$s \n", f.getName());
        }
        //  this.sLogLines = theLog;
        return theLog;
    }

    public static void main(String[] args) throws IOException {
        Server s = new Server();
    }
}
