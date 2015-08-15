/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Testtest;

import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author mamishev.d.a
 */
class ProcessResultReader extends Thread {

    final InputStream is;
    final String type;
    final StringBuilder sb;

    ProcessResultReader(InputStream is, String type) {
        this.is = is;
        this.type = type;
        this.sb = new StringBuilder();
    }

    public void run() {
        try {
            int z;
            while ((z = is.read()) != -1) {
                System.out.print((char) z);
            }

        } catch (final IOException ioe) {
            System.err.println(ioe.getMessage());
            throw new RuntimeException(ioe);
        }
    }

    @Override
    public String toString() {
        return this.sb.toString();
    }
}

public class RunBatTest {

    public void runBatFile(int apt) {
       // String res = null;
        try {
            String cmd = "cmd /c psexec -i \\\\10.77.2.21 -u exch1c\\pos_client -p zxcv_1234 cmd /c \"c:\\pos_client\\test\\a1.bat\" ";
            Runtime rt = Runtime.getRuntime();
            Date dt = new Date();
            SimpleDateFormat dft = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            cmd = cmd + "\"" + dft.format(dt) + "\"";
            Process pr = rt.exec(cmd);
            final ProcessResultReader stderr = new ProcessResultReader(pr.getErrorStream(), "STDERR");
            final ProcessResultReader stdout = new ProcessResultReader(pr.getInputStream(), "STDOUT");
            stderr.start();
            stdout.start();
            final int exitValue = pr.waitFor();
            if (exitValue == 0) {

                System.out.print(stdout.toString());
        //        res.concat(stdout.toString());
            } else {
                System.err.print(stderr.toString());
       //         res.concat(stdout.toString());
            }
        } catch (Exception e) {
            System.out.println(e.toString());
            e.printStackTrace();
          
       //     return res;
        }
    //    return res;
    }

    public static void main(String[] args) {
       RunBatTest t = new RunBatTest();
        t.runBatFile(5);
        t = null;
      
    }
}
