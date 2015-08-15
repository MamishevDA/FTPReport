/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package Testtest;

/**
 *
 * @author mamishev.d.a
 */
/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
import ftpreport.FTPReportGUI;
import ftpreport.FileDetails;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.Insets;
import java.awt.event.FocusEvent;
import java.awt.event.KeyAdapter;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JMenu;
import javax.swing.JMenuBar;
import javax.swing.JMenuItem;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JSeparator;
import javax.swing.JTable;
import javax.swing.JTextArea;
import javax.swing.JTextField;
import javax.swing.JTextPane;
import javax.swing.RowSorter;
import javax.swing.ScrollPaneConstants;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import jxl.biff.drawing.ComboBox;

/**
 *
 * @author mamishev.d.a
 */
class CustomTable extends JTable {

    public CustomTable(TableModel dm) {
        super(dm);
    }
 
    public String getToolTipText(java.awt.event.MouseEvent event) {
        String columnName = null;
        int columnNum = columnAtPoint(event.getPoint());
        columnName = MyTableModel.columnNames[columnNum];
        return "This is ToolTipText!"+columnName+"; num"+columnNum;
    }
}

class MyTableModel extends AbstractTableModel {

    private int i = 0;

    public MyTableModel(int i) {
        this.i = i;

    }

    public MyTableModel() {
    }
    static String[] columnNames = {"aptNum", "firstPack", "lastPack", "numPack", "pckName", "createDate", "sLogLines"};
    private Object[][] data = {{}};

    public int getColumnCount() {
        return columnNames.length;
    }

    public int getRowCount() {
        return 55;
    }

    public String getColumnName(int col) {
        return columnNames[col];
    }

    public Object getValueAt(int row, int col) {

        return row * col * 1150;
    }

    public Class getColumnClass(int c) {
        return getValueAt(0, c).getClass();
    }
}

class MSGText extends JFrame {

    MSGText(String sTitle) {
        setTitle(sTitle);
        getContentPane();
        setSize(320, 250);
    }
}

public class TestGUI extends JFrame {

    public TestGUI() {

        initComponents();
        createTableView();
    }

    private void jTableMouseClicked(java.awt.event.MouseEvent evt, int i, String str) {
        str = str + i + "\n";
        if (makeFrame.isSelected()) {
            txt.setText(str);
            msgFrame.add(txt);
            msgFrame.setVisible(true);

        } else {
            txt.setText(str);
            txtScroll.setViewportView(txt);
            super.add(txtScroll, BorderLayout.PAGE_END);
        }
    }

    private void mouseGetData(java.awt.event.MouseEvent evt) {
        createTableView();
        //addRightPanel(1);
        scrollPane.updateUI();

    }

    private void jTable1FocusGained(JTable tblL, FocusEvent evt) {
        //tblL.getSelectedRow();
        //   cb.setText(this.model.getAptLog(tbl.getSelectedRow()));
    }

    void createTableView() {
        try {
            UIManager.setLookAndFeel(metal);
        } catch (Exception ex) {
            Logger.getLogger(ftpreport.FTPReportGUI.class.getName()).log(Level.SEVERE, null, ex);
        }


        int v = ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED;

        int h = ScrollPaneConstants.HORIZONTAL_SCROLLBAR_AS_NEEDED;
        MyTableModel mtm = new MyTableModel(Integer.valueOf(countHoursTF.getText()));
        tbl = new CustomTable(mtm);
        tbl.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
            RowSorter<TableModel> sorter =
             new TableRowSorter<TableModel>(mtm);
           tbl.setRowSorter(sorter);
        
        System.out.println(tbl.getColumnModel().getColumn(2).getWidth());
        tbl.getColumnModel().getColumn(2).setMinWidth(200);
        System.out.println(tbl.getColumnModel().getColumn(2).getWidth());
        // tbl.setAutoResizeMode(JTable.AUTO_RESIZE_SUBSEQUENT_COLUMNS);
        scrollPane = new JScrollPane(tbl, v, h);
        // scrollPane.setHorizontalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);
        //scrollPane.setVerticalScrollBarPolicy(ScrollPaneConstants.VERTICAL_SCROLLBAR_AS_NEEDED);


        for (int i = 0; i < MyTableModel.columnNames.length; i++) {
            System.out.println(MyTableModel.columnNames[i].length());

            //  System.out.println(scrollPane.getViewport().getExtentSize() + " viewPort");

            tbl.getColumn(MyTableModel.columnNames[i]).setMinWidth(MyTableModel.columnNames[i].length() * 10);
        }


        super.getContentPane().add(scrollPane);
        scrollPane.updateUI();

        tbl.addFocusListener(new java.awt.event.FocusAdapter() {
            public void focusGained(java.awt.event.FocusEvent evt) {
                jTable1FocusGained(tbl, evt);
            }
        });
        tbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableMouseClicked(evt, tbl.getSelectedRow(), "the row is ");
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableMouseClicked(evt, tbl.getSelectedRowCount(), "rows count = ");
            }
        });



    }

    private void initComponents() {

        try {
            UIManager.setLookAndFeel(metal);
        } catch (Exception ex) {
            Logger.getLogger(ftpreport.FTPReportGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        super.setTitle("GUI");
        setSize(750, 450);

        //make right panel


        bGetData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mouseGetData(evt);

            }
        });

        JMenu mainMenu = new JMenu("Выбор основания для отчета");
        JMenuItem menuItemSourceFTP = new JMenuItem("построить по данным с FTP");

        menuItemSourceFTP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(
                    java.awt.event.ActionEvent e) {
                addRightPanel(0);
            }
        });
        JMenuItem menuItemSourceDB = new JMenuItem("построить по данным из БД");
        menuItemSourceDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(
                    java.awt.event.ActionEvent e) {
                addRightPanel(1);
            }
        });
        mainMenu.add(menuItemSourceFTP);
        mainMenu.add(menuItemSourceDB);
        JMenu a2 = new JMenu("АБАУТ");
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(mainMenu);
        menuBar.add(new JSeparator(1));
        menuBar.add(a2);
        setJMenuBar(menuBar);



        addRightPanel(0);


        txt.setSize(50, 50);
        txt.setRows(5);
        txtScroll.setViewportView(txt);
        add(txtScroll, BorderLayout.PAGE_END);

        super.add(rightPanel, BorderLayout.EAST);

        //add(mainPanel);


        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);

    }

    private void addRightPanel(int i) {

        GridBagConstraints for_bGetData = new GridBagConstraints(0, 1, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0);
        GridBagConstraints for_makeFrame = new GridBagConstraints(0, 2, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0);
        GridBagConstraints for_bMakeXLS = new GridBagConstraints(0, 3, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0);


        if (i == 0) {
            GridBagLayout gbl = new GridBagLayout();
            rightPanel.setLayout(gbl);
            rightPanel.removeAll();
            GridBagConstraints for_countPacksLbl = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 5);
            GridBagConstraints for_countPacksTF = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0);
            GridBagConstraints for_bDoExchange = new GridBagConstraints(0, 4, 2, 1, 0, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0);
            jScrollRemoveList.setViewportView(removeList);
            rightPanel.add(countPacksLbl, for_countPacksLbl);
            rightPanel.add(countPacksTF, for_countPacksTF);
            rightPanel.add(bGetData, for_bGetData);
            rightPanel.add(makeFrame, for_makeFrame);
            rightPanel.add(bMakeXLS, for_bMakeXLS);
            rightPanel.add(bDoExchange, for_bDoExchange);
            // rightPanel.add(cb);
        } else {
            rightPanel.removeAll();
            GridBagConstraints for_countHoursLbl = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 0, 2), 0, 5);
            GridBagConstraints for_countHoursTF = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0);
            GridBagConstraints for_bDoExchange = new GridBagConstraints(0, 4, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0);
            GridBagConstraints for_removeListLbl = new GridBagConstraints(0, 5, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0);
            GridBagConstraints for_jScrollRemoveList = new GridBagConstraints(1, 6, 1, 10, 0, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0);
            rightPanel.add(countHoursLbl, for_countHoursLbl);
            rightPanel.add(countHoursTF, for_countHoursTF);
            rightPanel.add(bGetData, for_bGetData);
            rightPanel.add(makeFrame, for_makeFrame);
            rightPanel.add(bMakeXLS, for_bMakeXLS);
            rightPanel.add(bDoExchange, for_bDoExchange);
            rightPanel.add(removeListLbl, for_removeListLbl);
            rightPanel.add(jScrollRemoveList, for_jScrollRemoveList);
        }
        rightPanel.updateUI();
        //
    }
    JTextField cb = new JTextField();
    private JTextArea txt = new JTextArea();
    private JScrollPane txtScroll = new JScrollPane();
    private JCheckBox makeFrame = new JCheckBox("Показать log в отдельном окне");
    private MSGText msgFrame = new MSGText("Log is");
    private JScrollPane scrollPane = null;
    TableColumn column = null;
    public JTextField countHoursTF = new JTextField("0", 5);
    final String windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    final String motif = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    final String metal = "javax.swing.plaf.metal.MetalLookAndFeel";
    JLabel countHoursLbl = new JLabel("count hours for report");
    JLabel countPacksLbl = new JLabel("count packs for report");
    JTextField countPacksTF = new JTextField("5", 5);
    JButton bGetData = new JButton("getData");
    JPanel rightPanel = new JPanel();
    CustomTable tbl = null;
    private JTextArea ta = new JTextArea("sdfsdf\n\nsdfsdf\n");
    private JScrollPane spta = new JScrollPane();
    private JLabel removeListLbl = new JLabel("Список исключаемых аптек");
    private JScrollPane jScrollRemoveList = new javax.swing.JScrollPane();
    private JTextArea removeList = new JTextArea("200136, \n"
            + "1600155, \n"
            + "5200009, \n"
            + "5200045, \n"
            + "5400229, \n"
            + "5920208, \n"
            + "5920234, \n"
            + "6620394, \n"
            + "7700106, ");
    private JButton bMakeXLS = new JButton("Выгрузить XLS");
    private JButton bDoExchange = new JButton("***Не выбрана строка***");

    public static void main(String[] args) {
        new TestGUI();

    }
}
