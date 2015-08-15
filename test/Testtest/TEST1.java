/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Testtest;

import java.awt.*;
import javax.swing.*;
import javax.swing.event.ChangeListener;
import javax.swing.event.ChangeEvent;

/**
 * GridBagLayoutTest
 *
 * @author Eugene Matyushkin
 */
public class TEST1 {

    public static void main(String[] args) {
        
        MSGText f = new MSGText("джоблю");
        f.setAlwaysOnTop(true);
        Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
        f.setBounds(sSize.width / 4, sSize.height / 4, 350, 45);
        System.out.println(sSize.getSize());
        //f.setLocation(w/4, h/4); f.setSize(350, 35);
        JProgressBar pBar = new JProgressBar();
        pBar.setIndeterminate(true);
        f.setVisible(true);
        f.add(pBar);
        f.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }
}