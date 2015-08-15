/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */

import ftpreport.*;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Locale;
import java.util.TreeMap;

/**
 *
 * @author mamishev.d.a
 */
public class DBConnectionTest {

    private static String sUsr = "sysadm";
    private static String sPwd = "sysadm";
    private static String url = "jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = localhost)(PORT = 1521)) (CONNECT_DATA =  (SERVER = DEDICATED) (SERVICE_NAME = XE)))";
    private static Connection con = null;
    private static TreeMap<Integer, TreeMap<String, String>> dbInfo = new TreeMap<Integer, TreeMap<String, String>>();

    public static Connection getConnect() throws SQLException {
        if (con == null || con.isClosed()) {
            try {
                System.out.println("соединение было закрыто или нет коннекта");
                Class.forName("oracle.jdbc.OracleDriver");
                Locale.setDefault(Locale.ENGLISH);
                con = DriverManager.getConnection(url, sUsr, sPwd);
                System.out.println("коннект получен");
                return con;
            } catch (Exception ee) {
                System.err.println(ee);
            }
        } else {
            return con;
        }
        return con;
    }

    public static TreeMap<Integer, TreeMap<String, String>> getDBInfo(String sQuery) {
        PreparedStatement prstm = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
//        for (int count = 0; count < 3; count++) {
        try {
            prstm = DBConnectionTest.getConnect().prepareStatement(sQuery);
            rs = prstm.executeQuery();
            rsmd = rs.getMetaData();
            System.out.println("данныые получил");
            int nColumnCount = rsmd.getColumnCount();
            boolean isFirstLine = true;
            while (rs.next()) {
                if (isFirstLine) {
                    isFirstLine = false;
                }
                TreeMap<String, String> dbValueTreeMap = new TreeMap<String, String>();
                for (int i = 1; i <= nColumnCount; i++) {
                    dbValueTreeMap.put(rsmd.getColumnName(i), rs.getString(i));
                }
                dbInfo.put(rs.getInt(1), dbValueTreeMap);
            }
            System.out.println("отдал и свалил с базы");
            return dbInfo;
        } catch (NullPointerException ex) {
            System.out.printf("Error in connection %1$s", ex);

        } catch (Exception ex) {
            System.out.printf("Error in connection %1$s", ex);
        } finally {
            try {
                if (prstm != null) {
                    prstm.close();
                }
                if (rs != null) {
                    rs.close();
                }
                con.close();
                if (rs != null) {
                    rs.close();
                }
                System.out.println("ВЫРУБАЮ ВСЕ НАХ");
            } catch (SQLException ex) {
                System.out.printf("error in ", ex);
            }
            //}
        }
        return null;
    }

    public static void printDBInfo() {
        for (Integer dbInfoKey : dbInfo.keySet()) {
            TreeMap<String, String> dbInfoValue = dbInfo.get(dbInfoKey);
            for (String colKey : dbInfoValue.keySet()) {
                System.out.printf("Key: %1$s Value: %2$s\r", colKey, dbInfoValue.get(colKey));
            }
        }
    }

    public static void main(String[] args) throws SQLException {
        //  DBConnectionTest db = new DBConnectionTest();
        //   db.getConnect();
        TreeMap<Integer, TreeMap<String, String>> tmp = getDBInfo("select * from pst_abonent_rcpt where abonent = 5200444");
        if (tmp.get(5200444) != null) {
            TreeMap<String, String> fd = tmp.get(5200444);
            printDBInfo();
            System.out.println(fd.get("LAST_RCVD_PACK"));
        }



        TreeMap<Integer, TreeMap<String, String>> tmp1 = getDBInfo("select * from pst_abonent_rcpt where abonent = 5200444");
        if (tmp1.get(5200444) != null) {
            TreeMap<String, String> fd = tmp1.get(5200444);
            printDBInfo();
            System.out.println(fd.get("LAST_RCVD_PACK"));
        }
    }
}
