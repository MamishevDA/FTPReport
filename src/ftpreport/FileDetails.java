/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ftpreport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.Field;
import java.net.InetAddress;
import java.net.Socket;
import java.net.UnknownHostException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.apache.commons.io.FileUtils;

/**
 *
 * @author mamishev.d.a
 */
public class FileDetails {

    public FileDetails(int numApt) {

        this.aptNum = numApt;
    }

    public String getLogFromServer(String path, Socket socket) {
        String sLog = null;
        try {
            dataIn = new DataInputStream(socket.getInputStream());
            dataOut = new DataOutputStream(socket.getOutputStream());
            dataOut.writeUTF(path);
            dataOut.flush();
            sLog = dataIn.readUTF();

        } catch (IOException ex) {
            sLog = "IOException in FileDetails.getLogFromServer";
        } finally {
            try {
                socket.close();
            } catch (IOException ex) {
                Logger.getLogger(FileDetails.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
        return sLog;
    }

    public String getLog(Socket socket) {
        int aptName = this.aptNum;
        int fromPosition = 0;
        String theLog = null;
        Pattern regex = Pattern.compile("-{50,}");
        String pathLog = "\\\\123123123\\pos_client\\log\\r" + aptName + ".log";

        //серверная часть программы не активна...
        if (socket != null) {
            theLog = getLogFromServer(pathLog, socket);
            if (theLog.length() > 0 || theLog != null) {
                this.sLogLines = theLog;
                return theLog;
            }
        }


        File f = new File(pathLog);
        theLog = "the Log of" + pathLog + "\n";
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
        this.sLogLines = theLog;
        return theLog;
    }

    public FileDetails addFileDetails(String sNamePack, Date createDate) {

        int curNumPck = 0;
        Pattern regex = Pattern.compile("[0-9]{8,}");
        Matcher m = regex.matcher(sNamePack);
        try {
            if (m.find()) {
                curNumPck = Integer.valueOf(m.group());
            }
        } catch (Exception e) {
            System.out.printf("Can't make the number from %1$s; error %2$s", m.group(), e);
        }
        if (this.firstPack == 0 || this.firstPack > curNumPck) {
            this.firstPack = curNumPck;
            this.lastPack = this.firstPack;
            this.pckName = sNamePack;
            this.createDate = createDate;
        } else {
            this.lastPack = curNumPck;
        }
        this.numPack = numPack + 1;
        return this;
    }

    public int getnumPack() {
        return this.numPack;
    }

    public int getAptNum() {
        return this.aptNum;
    }

    public Date getCreateDate() {
        return this.createDate;
    }

    public int getLastRCVD_pack_DB() {
        return this.lastRCVD_pack_DB;
    }

    public void setLastRCVD_pack_DB(int last_pack_date) {
        this.lastRCVD_pack_DB = last_pack_date;
    }

    public String getLAST_PACK_DATE() {
        return this.last_pack_date_DB;
    }

    public void setLAST_PACK_DATE(String last_pack_date) {
        this.last_pack_date_DB = last_pack_date;
    }

    public String getApt_perevod_date_DB() {
        return this.apt_perevod_date_DB;
    }

    public void setApt_perevod_date_DB(String apt_perevod_date_DB) {
        this.apt_perevod_date_DB = apt_perevod_date_DB;
    }

    public String getApt_closed_date_DB() {
        return this.apt_closed_date_DB;
    }

    public void setApt_closed_date_DB(String apt_closed_date_DB) {
        this.apt_closed_date_DB = apt_closed_date_DB;
    }

    @Deprecated
    public String getValueOf(Integer i) {
        switch (i) {
            case 0:
                return String.valueOf(this.aptNum);
            case 1:
                return String.valueOf(this.firstPack);
            case 2:
                return String.valueOf(this.lastPack);
            case 3:
                return String.valueOf(this.numPack);
            case 4:
                return this.pckName;
            case 5:
                return String.valueOf(this.createDate);
            case 6:
                return this.sLogLines;
        }
        return "this." + allFields[i];
    }

    public String getValueOf(int i) {
        try {
            Field fld = FileDetails.class.getDeclaredField(allFields[i]);
            Object obj = fld.get(this);
            if (obj instanceof Date) {
                return dateFormat.format(this.createDate);
            }
            return obj != null ? obj.toString() : null;
            // return  fld.get(this).toString();
        } catch (Exception ex) {
            Logger.getLogger(FileDetails.class.getName()).log(Level.SEVERE, null, ex);
        }
        return "";

    }

    public void setLogLines(String sLog) {
        this.sLogLines = sLog;
    }
    private int aptNum = 0;
    private String apt_perevod_date_DB = null;
    private String apt_closed_date_DB = null;
    private int lastRCVD_pack_DB = 0;
    private String last_pack_date_DB = null;
    private int firstPack = 0;
    private int lastPack = 0;
    private int numPack = 0;
    private String pckName = null;
    private Date createDate = null;
    private String sLogLines = null;
    static String[] allFields = {"aptNum", "apt_perevod_date_DB", "apt_closed_date_DB", "lastRCVD_pack_DB", "last_pack_date_DB", "firstPack", "lastPack", "numPack", "pckName", "createDate", "sLogLines"};
    static private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy HH:mm:ss");
    private DataInputStream dataIn = null;
    private DataOutputStream dataOut = null;
//    @Override
//    public String toString() {
//
//        return String.format("%1$s; apt_perevod_date_DB is %2$s; apt_closed_date_DB is %3$s; lastRCVD_pack_DB is %4$s; last_pack_date_DB is %5$s; first is %6$s; last is %7$s; count = %8$s; the file is %9$s; %10$s; Log is%11$s",
//                this.aptNum, this.apt_perevod_date_DB, this.apt_closed_date_DB, this.lastRCVD_pack_DB, this.last_pack_date_DB, this.firstPack, this.lastPack, this.numPack, this.pckName,
//                dateFormat.format(this.createDate), this.sLogLines);
//    }
}