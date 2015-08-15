/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Testtest;

/**
 *
 * @author mamishev.d.a
 */

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 * MouseTest
 *
 * @author Eugene Matyushkin aka Skipy
 * @since 10.08.2010
 */
public class MouseTest extends JFrame {

    public MouseTest(){
        super("Mouse events test");
        JButton btn = new JButton("Press me");
        btn.addMouseListener(new MouseListenerImpl());
        btn.addActionListener(new ActionListener(){
            @Override
            public void actionPerformed(ActionEvent e) {
                System.out.println("Action performed");
            }
        });
        getContentPane().add(btn, BorderLayout.SOUTH);
        setSize(300,200);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new MouseTest().setVisible(true);
    }

    private static class MouseListenerImpl extends MouseAdapter {

        @Override
        public void mouseClicked(MouseEvent e) {
            System.out.println("Mouse Clicked");
        }

        @Override
        public void mousePressed(MouseEvent e) {
            System.out.println("Mouse Pressed");
        }

        @Override
        public void mouseReleased(MouseEvent e) {
            System.out.println("Mouse Released");
        }
    }
}