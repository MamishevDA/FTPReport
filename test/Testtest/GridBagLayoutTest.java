package Testtest;

import java.awt.*;
import javax.swing.JButton;
import javax.swing.JFrame;
import javax.swing.JTextArea;

public class GridBagLayoutTest extends JFrame {
    public GridBagLayoutTest(){
        init();
    }
    public void init(){
        super.setTitle("we'll see, is it so hard");
        //super.setSize(150, 150);
        super.setMinimumSize(new Dimension(200,200));
        super.setMaximumSize(new Dimension(300,250));
        
        GridBagLayout gbl = new GridBagLayout();
        super.setLayout(gbl);
        JButton b1 = new JButton("b1");
        JButton b2 = new JButton("b2");
        JTextArea txt = new JTextArea("sdfasdfasdfasdfasdfasdfasdfsdfasd\nsdnasdfsdf");
        GridBagConstraints forTxt = new GridBagConstraints(0,0,1,1,1,1,GridBagConstraints.CENTER,GridBagConstraints.BOTH,new Insets(1,1,1,1),0,0);
        GridBagConstraints forButton1 = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTH,GridBagConstraints.NONE,new Insets(1,1,1,1),0,0);
        GridBagConstraints forButton2 = new GridBagConstraints(1,0,1,1,0,0,GridBagConstraints.NORTH,GridBagConstraints.NONE,new Insets(55,5,5,5),0,0);
        gbl.setConstraints(txt, forTxt);
        gbl.setConstraints(b1, forButton1);
        gbl.setConstraints(b2, forButton2);
        add(txt);
        add(b1);
        add(b2);
        super.setVisible(true);
        super.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
    }

    public static void main(String[] args) {
        new GridBagLayoutTest();
    }
}