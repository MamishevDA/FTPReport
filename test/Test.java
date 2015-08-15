
import java.io.IOException;
import java.net.SocketException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.net.ftp.FTPClient;


/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mamishev.d.a
 */
class MyDate extends Date {

    GregorianCalendar s = new GregorianCalendar();
//    @Override
//    public String toString(){
//        return "year";
    //}
}

public class Test {

    public static void main(String[] args) {
        String str = "7700044";
        System.out.println((str.length() < 8) ? "not a zip package" : str.substring(1, 8));

        try {
            FTPClient ftp = new FTPClient();
            ftp.connect("10.77.0.241");
            ftp.login("anonymous", "anonymous");
            //FTPClientConfig conf = new FTPClientConfig(FTPClientConfig.SYST_VMS);
            //conf.setDefaultDateFormatStr("dd mm yyyy");
            //ftp.configure(conf);
            ftp.changeWorkingDirectory("/pub/1c/s2w/");


            // FTPFile[] allFiles = ftp.listFiles();
            System.out.println("u0200063-0000000518.zip".indexOf("8.zip"));
            String str1 = "u0200001-0000000471.zip";
            System.out.println(str1.substring(str1.indexOf("-") + 1, str1.indexOf(".zip")));
            System.out.println(str1.matches("^u||U(0-9)"));


            Pattern regex = Pattern.compile("[0-9]{8,50}");
            Matcher m = regex.matcher(str1);
            String[] pckInfo = {"apt", "snum"};
            int i = 0;
            while (m.find()) {

                pckInfo[i] = m.group();
//                System.out.println(m.group(0));
//                System.out.println(m.group(1));
                i += 1;
            }
            System.out.println(pckInfo[0] + "---" + pckInfo[1]);
            SimpleDateFormat t = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
            GregorianCalendar sysdate = new GregorianCalendar();

            GregorianCalendar sysdate1 = new GregorianCalendar();
            sysdate1.roll(GregorianCalendar.HOUR, -5);
            sysdate1.getTime();

            System.out.println(sysdate1.getTime().compareTo(sysdate.getTime()));
            
            ftp.logout();

        } catch (SocketException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(Test.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}
