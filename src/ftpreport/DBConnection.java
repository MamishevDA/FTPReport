/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ftpreport;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.util.Locale;
import java.util.TreeMap;
import javax.swing.JOptionPane;

/**
 *
 * @author mamishev.d.a
 */
public class DBConnection {

//    private static String sUsr = "sysadm";
//    private static String sPwd = "sysadm";
//    private static String url = "jdbc:oracle:thin:@(DESCRIPTION = (ADDRESS = (PROTOCOL = TCP)(HOST = localhost)(PORT = 1521)) (CONNECT_DATA =  (SERVER = DEDICATED) (SERVICE_NAME = XE)))";
    private static String sUsr = "";
    private static String sPwd = "";
    private static String url = "jdbc:oracle:thin:@(DESCRIPTION =\n"
            + "    (ADDRESS = (PROTOCOL = TCP)(HOST = 123123123)(PORT = 1521))\n"
            + "    (ADDRESS = (PROTOCOL = TCP)(HOST = 123123123)(PORT = 1521))\n"
            + "    (LOAD_BALANCE = yes)\n"
            + "    (CONNECT_DATA =\n"
            + "      (SERVER = DEDICATED)\n"
            + "      (SERVICE_NAME = dc)\n"
            + "      (FAILOVER_MODE =\n"
            + "        (TYPE = SELECT)\n"
            + "        (METHOD = BASIC)\n"
            + "        (RETRIES = 180)\n"
            + "        (DELAY = 5)\n"
            + "      )\n"
            + "    )\n"
            + "  )";
    private static Connection con = null;
    private static TreeMap<Integer, TreeMap<String, String>> dbInfo = new TreeMap<Integer, TreeMap<String, String>>();

    public static Connection getConnect() throws SQLException {
        if (con == null || con.isClosed()) {
            try {
                System.out.println("the connection has been closed or not connected");
                Class.forName("oracle.jdbc.OracleDriver");
                Locale.setDefault(Locale.ENGLISH);
                con = DriverManager.getConnection(url, sUsr, sPwd);
                System.out.println("get connect for pos_gate");
                return con;
            } catch (Exception ee) {
                System.err.println(ee);
            }
        } else {
            return con;
        }
        return con;
    }

    public static TreeMap<Integer, TreeMap<String, String>> getDBInfo(String removeList, int daysBefore) {
        PreparedStatement prstm = null;
        ResultSet rs = null;
        ResultSetMetaData rsmd = null;
        /* нужно же чистить статик переменную
         * т.к. использую TreeMap не очень страшно
         * но если что-то в базе изменится, мой набор будет не правдоподобен
         * т.к. не обновлю удаленную строку
         *  */
        dbInfo.clear();
//        for (int count = 0; count < 3; count++) {
        try {
            String sQuery = "select al.loc ABONENT,\n"
                    + "       to_char(al.perevod_date, 'dd.mm.yyyy') APT_PEREVOD_DATE_DB,\n"
                    + "       to_char(ml.closed, 'dd.mm.yyyy') APT_CLOSED_DATE_DB,\n"
                    + "       r.last_rcvd_pack LAST_RCVD_PACK,\n"
                    + "       to_char(LAST_PACK_DATE, 'dd.mm.yyyy hh24:mi:ss') LAST_PACK_DATE\n"
                    + "  from am$loc_shift al, pst_abonent_rcpt r, md_location ml\n"
                    + " where al.loc = r.abonent\n"
                    + "   and al.loc = ml.loc"
                    + "   and trunc(r.last_pack_date) < sysdate - ?\n"
                    + "   and trunc(al.perevod_date) < sysdate - ?"
                    + "and al.loc not in (" + removeList + ")";
            prstm = DBConnection.getConnect().prepareStatement(sQuery);
            prstm.setInt(1, daysBefore);
            prstm.setInt(2, daysBefore);
            //   prstm.setString(3, removeList);
            rs = prstm.executeQuery();
            rsmd = rs.getMetaData();
            System.out.println("get all data from DB");
            int nColumnCount = rsmd.getColumnCount();
            boolean isFirstLine = true;
            while (rs.next()) {
                if (isFirstLine) {
                    isFirstLine = false;
//                for (int n = 1; n <= nColumnCount; n++) {
//                    System.out.print(rsmd.getColumnName(n) + "  ");
//                }
//                System.out.print("\n");
                }
                TreeMap<String, String> dbValueTreeMap = new TreeMap<String, String>();
                for (int i = 1; i <= nColumnCount; i++) {
                    dbValueTreeMap.put(rsmd.getColumnName(i), rs.getString(i));
                }
                dbInfo.put(rs.getInt(1), dbValueTreeMap);
            }
            // System.out.println("connection try count = " + count);
            System.out.println("get the link on DB and go away");
            return dbInfo;
        } catch (NullPointerException ex) {
            System.out.printf("Error in connection %1$s\n", ex);

        } catch (Exception ex) {
            System.out.printf("Error in connection %1$s\n", ex);

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
                System.out.println("Close all connect");
            } catch (SQLException ex) {
                System.out.printf("error in ", ex);
                JOptionPane.showMessageDialog(null, "Ошибка подключения к БД\n данные по аптекам не получены\n нажмите \"Получить данные\"", "Error in connection", 1);

            } catch (NullPointerException ex) {
                JOptionPane.showMessageDialog(null, "Ошибка подключения к БД\n попробуйте еще раз...", "Error in connection", 1);
            }
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
    }
}
