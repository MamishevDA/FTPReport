/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Testtest;

//import com.sun.xml.internal.ws.util.StringUtils;
import java.awt.List;
import java.sql.Array;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 *
 * @author mamishev.d.a
 */
public class TestParseRemoveList {

    public static void main(String args[]) {
        String removeList = "200136, \n"
                + "1600155,\n"
                + "5200009,\n"
                + "5200045,\n"
                + "5400229,\n"
                + "5920208,\n"
                + "5920234,\n"
                + "6620394,\n"
                + "7700106,\n"
                + "200174";

        Pattern regex = Pattern.compile("[0-9]{6,7}");
        Matcher m = regex.matcher(removeList);
        String res = "";
        while (m.find()) {
             res += "," + m.group();
        }
        System.out.println(res.substring(1));


    }
}
