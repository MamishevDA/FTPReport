/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ftpreport;

import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.net.InetAddress;
import java.net.Socket;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.sql.SQLException;
import java.text.SimpleDateFormat;
import java.util.GregorianCalendar;
import java.util.TreeMap;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import javax.swing.JOptionPane;
import org.apache.commons.net.ftp.*;

/**
 *
 * @author mamishev.d.a
 */
class CustomSocket extends Socket{

    private static int port = 6666;
    private static String serverAddress = "123123123";
    private static CustomSocket socket = null;
    private static InetAddress server = null;
    
     private CustomSocket(InetAddress inetAddress,int port) throws IOException {
        super(inetAddress, port);
    }

    public static CustomSocket getCustomSocket()  {
        if (socket == null || socket.isClosed()) {
            try {
                server = InetAddress.getByName(serverAddress);
                socket = new CustomSocket(server, port);
            } catch (IOException ex) {
                socket = null;
                System.out.printf("server not available on %1$s:%2$s\n", serverAddress,port);
            }
        }
        return socket;
    }
}

public class FTPReport {

    FTPReport(boolean doByFTP, int numHours, int numPack, String removeList) {
        TreeMap<Integer, FileDetails> tmpMap = new TreeMap<Integer, FileDetails>();
        try {
            FTPClient ftp = new FTPClient();
            ftp.connect("123123123");
            ftp.login("anonymous", "anonymous");
            //ftp.changeWorkingDirectory("/pub/1c/w2s/");
            ftp.changeWorkingDirectory("/pub/1c/s2w/");
            FTPFile[] fileList = ftp.listFiles("", new FTPFileFilter() {
                @Override
                public boolean accept(FTPFile ftpf) {
                    return ftpf.getName().endsWith(".zip");
                }
            });
            for (int i = 0; i < fileList.length; i++) {
                Pattern regex = Pattern.compile("[0-9]{7,}");
                Matcher m = regex.matcher(fileList[i].getName());
                if (m.find()) {
                    Integer apt = Integer.valueOf(m.group());
                    //if (apt == 7700027) {
                    if (tmpMap.containsKey(apt)) {
                        fileList[i].getTimestamp();
                        tmpMap.get(apt).addFileDetails(fileList[i].getName(), fileList[i].getTimestamp().getTime());
                    } else {
                        tmpMap.put(apt, new FileDetails(apt).addFileDetails(fileList[i].getName(), fileList[i].getTimestamp().getTime()));
                    }
                    // }
                } else {
                    System.out.printf("Not correct file: %1$s\n", fileList[i].getName());
                }
//                if (doPrint) {
//                    System.out.println(fileList[i]);
//                }
            }
            //ver 0.1.5
            //DBConnection db = new DBConnection();
            if (doByFTP) {
                numHours = 0;
                removeList = "-666";
            }
            //System.out.println(removeList);
            TreeMap<Integer, TreeMap<String, String>> dbInfoLink = DBConnection.getDBInfo(removeList, numHours);
            /*
             * Получил все данные, можно формировать по FTP*/
            if (doByFTP) {
                for (FileDetails info : tmpMap.values()) {
                    //if (sysdate1.getTime().compareTo(info.getCreateDate()) < 0 && info.getnumPack() > 3) {
                    //&& info.getCreateDate().compareTo(sysdate.getTime()) < 0
                    if (info.getnumPack() > numPack) {
                        try {
                            // tmp = dbInfoLink.get(info.getAptNum());
                            if (dbInfoLink.get(info.getAptNum()) != null) {
                                info.setLastRCVD_pack_DB(Integer.valueOf(dbInfoLink.get(info.getAptNum()).get("LAST_RCVD_PACK")));
                                info.setLAST_PACK_DATE(dbInfoLink.get(info.getAptNum()).get("LAST_PACK_DATE"));
                                info.setApt_perevod_date_DB(dbInfoLink.get(info.getAptNum()).get("APT_PEREVOD_DATE_DB"));
                                info.setApt_closed_date_DB(dbInfoLink.get(info.getAptNum()).get("APT_CLOSED_DATE_DB"));
                            }
                        } catch (NullPointerException ex) {
                            info.setApt_perevod_date_DB("  ***  ");
                            info.setApt_closed_date_DB("  ***  ");
                            info.setLastRCVD_pack_DB(-666);
                            info.setLAST_PACK_DATE("DB connection error");
                           // System.out.printf("Not connecting to DB %1$s\n", ex);
                        }
                        //myMap.get(info.getAptNum());
                        //    info.setLogLines(info.getLog());
                        myMap.put(info.getAptNum(), info);
                        //  System.out.println(info.toString());
                    }
                }
            } else {
                /*
                 иначе собирем инфу по БД*/
                for (Integer dbInfoLinkVal : dbInfoLink.keySet()) {
                    FileDetails tmpSource = null;
                    if (tmpMap.containsKey(dbInfoLinkVal)) {
                        tmpSource = tmpMap.get(dbInfoLinkVal);
                    } else {
                        tmpSource = new FileDetails(dbInfoLinkVal);
                    }
                    TreeMap<String, String> aptLine = dbInfoLink.get(dbInfoLinkVal);
                    tmpSource.setLAST_PACK_DATE(aptLine.get("LAST_PACK_DATE"));
                    tmpSource.setLastRCVD_pack_DB(Integer.valueOf(aptLine.get("LAST_RCVD_PACK")));
                    tmpSource.setApt_closed_date_DB(aptLine.get("APT_CLOSED_DATE_DB"));
                    tmpSource.setApt_perevod_date_DB(aptLine.get("APT_PEREVOD_DATE_DB"));
                    myMap.put(dbInfoLinkVal, tmpSource);
                }
            }
            ftp.logout();
            rowIndex = myMap.keySet().toArray(new Integer[0]);
        } catch (NullPointerException ex) {
            JOptionPane.showMessageDialog(null, "Ошибка подключения к БД\n попробуйте еще раз...", "Error in connection", 1);


        } catch (SocketException ex) {
            Logger.getLogger(FTPReport.class.getName()).log(Level.SEVERE, null, ex);
        } catch (IOException ex) {
            Logger.getLogger(FTPReport.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    void getInfo(boolean doOutputInConsole) {
        System.out.println("***************");
        for (FileDetails info : myMap.values()) {
            GregorianCalendar sysdate1 = new GregorianCalendar();
            sysdate1.roll(GregorianCalendar.HOUR, -5);
            sysdate1.getTime();
            //if (sysdate1.getTime().compareTo(info.getCreateDate()) < 0 && info.getnumPack() > 3) {
            if (info.getnumPack() > 30 || doOutputInConsole) {
                info.setLogLines(info.getLog(CustomSocket.getCustomSocket()));
                if (doOutputInConsole) {
                    System.out.println(info.toString());
                }
            }
        }
    }

    String getAptLog(int row) {
        //плохо! нужно допилить определение кода лога
        String tmp = myMap.get(rowIndex[row]).getValueOf(10);
        timeFrom  = new GregorianCalendar();
        if (tmp == null) {
            System.out.printf("make new request for log by %1$s",myMap.get(rowIndex[row]).getAptNum());
            tmp = myMap.get(rowIndex[row]).getLog(CustomSocket.getCustomSocket());
        }
        timeTo = new GregorianCalendar();
        System.out.printf("; spend time for request = %1$s ms\n", timeTo.getTime().getTime()-timeFrom.getTime().getTime());
        return tmp;
    }

    public void updateLogsData() {
        for (FileDetails info : myMap.values()) {
            info.setLogLines(info.getLog(CustomSocket.getCustomSocket()));
        }
    }

    public Integer getKey(Integer index) {
        return rowIndex[index];
    }

    public int getCountMap() {
        return myMap.size();
    }

    public String getValueAtMap(int row, int col) {
        return myMap.get(rowIndex[row]).getValueOf(col);
    }
    private TreeMap<Integer, FileDetails> myMap = new TreeMap<Integer, FileDetails>();
    static Integer[] rowIndex = new Integer[]{};

    
    //for tests only
    static GregorianCalendar timeFrom = null;
    static GregorianCalendar timeTo = null;
    public static void main(String[] args) {
        //new FTPReport(false).getInfo();
        FTPReport t = new FTPReport(false, 3, 3, "df");
        // t.getInfo(true);
        //t.getAptLog(0);


    }
}
