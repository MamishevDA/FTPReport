
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.IOUtils.*;
import org.apache.commons.net.ftp.FTPClient;

/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
/**
 *
 * @author mamishev.d.a
 */
public class ParseLog {

    public String makeLog(Integer aptName) {
        int fromPosition = 0;
        String theLog = "";
        Pattern regex = Pattern.compile("-{50,}");

        String pathLog = "\\\\10.77.2.21\\pos_client\\log\\s" + aptName.toString() + ".log";
        File f = new File(pathLog);
        if (f.exists()) {
            try {
                System.out.printf("the file is %1$s \n", f.getAbsolutePath());
                List<String> l = FileUtils.readLines(f);
                String[] allLines = l.toArray(new String[0]);
                for (int i = allLines.length - 1; i >= 0; i--) {
                    Matcher m = regex.matcher(allLines[i]);
                    //найдем позицию с которой тянуть лог, можкт учитывать ошибки ORA?
                    if (m.find() && fromPosition == 0) {
                        fromPosition = i;
                        System.out.println(allLines[i] + " i is " + fromPosition);
                        break;
                    }
                }
                String sLog[] = l.subList(fromPosition, l.size()).toArray(new String[0]);
                for (String s : sLog) {
                    theLog = theLog + s + "\n";
                }
            } catch (IOException ex) {
                Logger.getLogger(ParseLog.class.getName()).log(Level.SEVERE, null, ex);
            }
        } else {
            System.out.printf("Can't found log  %1$s \n", f.getName());
        }
        return theLog;
    }

    public static void main(String[] args) {

        String pathLog1 = "\\10.77.2.21\\pos_client\\log";
        File f = new File(pathLog1);
        //ftp.changeWorkingDirectory("/pos_client/log/");
//        for (String s : f.list()) {
//            System.out.println(s);
//        }


        System.out.println(new ParseLog().makeLog(3407406));
        System.out.println(new ParseLog().makeLog(7700057));



}
}
