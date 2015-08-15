/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package ftpreport;

/**
 *
 * @author mamishev.d.a
 * @version 0.1.5
 *
 *
 */
import java.awt.*;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.util.Locale;
import java.util.regex.Matcher;
import javax.swing.table.AbstractTableModel;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableModel;
import javax.swing.table.TableRowSorter;
import jxl.Workbook;
import jxl.WorkbookSettings;
import jxl.format.*;
import jxl.write.Label;
import jxl.write.WritableCellFormat;
import jxl.write.WritableFont;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.WriteException;
import jxl.write.biff.RowsExceededException;

/* наврядли нужен тут поток, нужно подумать
 * 
 */
class DocExportXLS extends Thread implements Runnable {

    private MyTableModel modelLink = null;

    public DocExportXLS(MyTableModel modelLink) throws IOException, WriteException {
        makeFile(modelLink);
        this.modelLink = modelLink;
    }
    private static WritableWorkbook workbook; // переменная рабочей книги
    public static WritableSheet sheet;
    public static WritableCellFormat arial12BoldFormat;
    public static Label fileLine;

    void makeFile(MyTableModel modelLink) throws IOException, WriteException {
        WritableFont arial8ptBold =
                new WritableFont(WritableFont.ARIAL, 8, WritableFont.NO_BOLD);
        arial12BoldFormat = new WritableCellFormat(arial8ptBold);
        // arial12BoldFormat.setWrap(true);
        workbook = Workbook.createWorkbook(new File("c:\\unload.xls"));
        sheet = workbook.createSheet("Create from GUIFtpreport", 0);

        for (int i = 0; i < modelLink.getRowCount(); i++) {
            modelLink.getAptLog(i);
            for (int j = 0; j < modelLink.getColumnCount(); j++) {
                fileLine = new Label(j, i, modelLink.getValueAt(i, j).toString(), arial12BoldFormat);
                sheet.addCell(fileLine);
            }
        }
        workbook.write();
        workbook.close();
    }

    public void run() {
        try {
            makeFile(this.modelLink);
        } catch (IOException ex) {
            Logger.getLogger(DocExportXLS.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WriteException ex) {
            Logger.getLogger(DocExportXLS.class.getName()).log(Level.SEVERE, null, ex);
        }
    }
}

class CustomTable extends JTable {

    public CustomTable(TableModel dm) {
        super(dm);
        /*кривая сортировка, на тест*/
        RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(dm);
        setRowSorter(sorter);
    }

    public String getToolTipText(java.awt.event.MouseEvent event) {
        String columnName = null;
        int columnNum = columnAtPoint(event.getPoint());
        columnName = MyTableModel.columnNamesRus[columnNum];
        return columnName;
    }
}

class MyTableModel extends AbstractTableModel {

    public MyTableModel(boolean doByFTP, int numHours, int numPack, String removeList) {
        this.doByFTP = doByFTP;
        this.numHours = numHours;
        this.numPack = numPack;
        this.removeList = removeList;
        this.FTPReportSource = getFTPReportSource();

    }
    private boolean doByFTP = false;
    private int numHours = 50;
    private int numPack = 50;
    private String removeList = null;
    private static String[] columnNames = FileDetails.allFields;
    final static String[] columnNamesRus = {"Аптека", "Дата перевода аптеки(из базы)", "Дата закрытия аптеки(из базы)", "Последняя принятая посылка(из базы)", "Дата последней посылки(из базы)", "Номер первой найденной посылки на ftp", "Номер последней найденной посылки на ftp", "Количество посылок на ftp", "Имя посылки", "Дата создания посылки", "Лог"};
    private FTPReport FTPReportSource = null;
    private boolean isModelChange = true;

    FTPReport getFTPReportSource() {
        // if (FTPReportSource == null) {

        FTPReportSource = new FTPReport(doByFTP, numHours, numPack, removeList);
        // FTPReportSource.getInfo(true);
        //     return FTPReportSource;
        // } else {
        return FTPReportSource;
        // }
    }

    public void setSourceForReport(boolean doByFTP) {
        this.doByFTP = doByFTP;
    }

    public static String[] getColumnNames() {
        return columnNames;
    }

    @Override
    public int getColumnCount() {
        return columnNames.length;
    }

    @Override
    public int getRowCount() {
        return FTPReportSource.getCountMap();
    }

    @Override
    public String getColumnName(int col) {
        return columnNames[col];
    }

    @Override
    public Object getValueAt(int row, int col) {
        return FTPReportSource.getValueAtMap(row, col);
    }

//    @Override
//    public Class getColumnClass(int c) {
//        return getValueAt(0, c).getClass();
//    }
    public String getAptLog(int i) {
        return FTPReportSource.getAptLog(i);
    }

    public void updateLogsData() {
        FTPReportSource.updateLogsData();
    }

    public boolean getIsModelChange() {
        return this.isModelChange;
    }

    public void setModelChange() {
        this.isModelChange = false;
    }
}

class MSGText extends JFrame {

    MSGText(String sTitle) {
        setTitle(sTitle);
        Dimension sSize = Toolkit.getDefaultToolkit().getScreenSize();
        setBounds(sSize.width / 10, sSize.height / 10, 1150, 375);
    }
}

public class FTPReportGUI extends JFrame {

    public FTPReportGUI() {
        this.model = new MyTableModel(this.doByFTP, Integer.valueOf(countDaysTF.getText()), Integer.valueOf(countPacksTF.getText()), validateRemoveList(removeList.getText()));
        initComponents();
    }
    MyTableModel model = null;

    private void jTableMouseClicked(CustomTable tbl, java.awt.event.MouseEvent evt) {
        String sLogMsg = this.model.getAptLog(tbl.getSelectedRow());
        //обмен не подключен
       // bDoExchange.setText("Провести обмен по аптеке " + this.model.getValueAt(tbl.getSelectedRow(), 0));

        if (makeFrame.isSelected()) {
            txtOnMsgFrame.setText(sLogMsg);
            txtOnMsgFrame.setEditable(false);
            txtScrollOnMsgFrame.setViewportView(txtOnMsgFrame);
            msgFrame.add(txtScrollOnMsgFrame);
            msgFrame.setVisible(true);
        } else {
            txtOnMainFrame.setText(sLogMsg);
            txtOnMainFrame.setEditable(false);
        }
    }

    private void jTableMouseReleased(CustomTable tbl, java.awt.event.MouseEvent evt) {
        txtOnMainFrame.setText(String.format("Selected row count = %1$s", tbl.getSelectedRowCount()));
//        int[] i = tbl.getSelectedRows();
//        String strTmp = null;
//        for (int tmp : i) {
//            strTmp = strTmp + this.model.getAptLog(tmp) + "\n";
//        }
//        txtOnMainFrame.setText(strTmp);
//        txtOnMainFrame.append(String.format("Selected row count = %1$s", tbl.getSelectedRowCount()));
    }

    private void mouseGetData(java.awt.event.MouseEvent evt) {
        createTableView();
    }

    private void mouseMakeXLS(java.awt.event.MouseEvent evt) {
        makeXLS();
    }

    void createTableView() {
        try {
            UIManager.setLookAndFeel(metal);
        } catch (Exception ex) {
            Logger.getLogger(ftpreport.FTPReportGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        this.model = new MyTableModel(this.doByFTP, Integer.valueOf(countDaysTF.getText()), Integer.valueOf(countPacksTF.getText()), validateRemoveList(removeList.getText()));
        this.tbl.setModel(this.model);
        setTableColumnWidth(tbl);

        /*кривая сортировка, на тест*/
        RowSorter<TableModel> sorter = new TableRowSorter<TableModel>(this.model);
        this.tbl.setRowSorter(sorter);

    }

    void makeXLS() {
        MSGText f = new MSGText("джоблю");
        JProgressBar pBar = new JProgressBar();
        pBar.setIndeterminate(true);
        f.setVisible(true);
        f.add(pBar, BorderLayout.CENTER);
        try {
            if (this.model.getIsModelChange()) {
                this.model.updateLogsData();
                this.model.setModelChange();
            }
            new DocExportXLS(this.model);
            f.setVisible(false);
        } catch (IOException ex) {
            Logger.getLogger(FTPReportGUI.class.getName()).log(Level.SEVERE, null, ex);
        } catch (WriteException ex) {
            Logger.getLogger(FTPReportGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

    //подтянем с базы все номера посылок
    void createRightPanel(String doWhat) {
        setTitle("GUI FTPReport ver 0.1.8 работаю по " + doWhat);
        GridBagLayout gbl = new GridBagLayout();
        rightPanel.setLayout(gbl);
        jScrollRemoveList.setViewportView(removeList);
        GridBagConstraints for_bGetData = new GridBagConstraints(0, 2, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0);
        GridBagConstraints for_makeFrame = new GridBagConstraints(0, 3, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0);
        GridBagConstraints for_bMakeXLS = new GridBagConstraints(0, 4, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0);
        if (doWhat.equals("FTP")) {
            setDoByFTP(true);
            rightPanel.removeAll();
            GridBagConstraints for_countPacksLbl = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 5);
            GridBagConstraints for_countPacksTF = new GridBagConstraints(0, 1, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0);
            GridBagConstraints for_bDoExchange = new GridBagConstraints(0, 5, 2, 1, 0, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0);
            jScrollRemoveList.setViewportView(removeList);
            rightPanel.add(countPacksLbl, for_countPacksLbl);
            rightPanel.add(countPacksTF, for_countPacksTF);
            rightPanel.add(bGetData, for_bGetData);
            rightPanel.add(makeFrame, for_makeFrame);
            rightPanel.add(bMakeXLS, for_bMakeXLS);
            rightPanel.add(bDoExchange, for_bDoExchange);

        } else if (doWhat.equals("DB")) {
            setDoByFTP(false);
            rightPanel.removeAll();
            GridBagConstraints for_countDaysLbl = new GridBagConstraints(0, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.NONE, new Insets(2, 2, 0, 2), 0, 5);
            GridBagConstraints for_countDaysTF = new GridBagConstraints(1, 0, 1, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 2, 2), 0, 0);
            GridBagConstraints for_bDoExchange = new GridBagConstraints(0, 5, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.HORIZONTAL, new Insets(2, 2, 0, 2), 0, 0);
            GridBagConstraints for_removeListLbl = new GridBagConstraints(0, 6, 2, 1, 0, 0, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0);
            GridBagConstraints for_jScrollRemoveList = new GridBagConstraints(1, 7, 1, 10, 0, 1, GridBagConstraints.NORTHWEST, GridBagConstraints.BOTH, new Insets(2, 2, 0, 2), 0, 0);
            rightPanel.add(countDaysLbl, for_countDaysLbl);
            rightPanel.add(countDaysTF, for_countDaysTF);
            rightPanel.add(bGetData, for_bGetData);
            rightPanel.add(makeFrame, for_makeFrame);
            rightPanel.add(bMakeXLS, for_bMakeXLS);
            rightPanel.add(bDoExchange, for_bDoExchange);
            rightPanel.add(removeListLbl, for_removeListLbl);
            rightPanel.add(jScrollRemoveList, for_jScrollRemoveList);
        } else {
            rightPanel.removeAll();
        }
        rightPanel.updateUI();
    }

    public String validateRemoveList(String removeList) {
        java.util.regex.Pattern regex = java.util.regex.Pattern.compile("[0-9]{6,7}");
        Matcher m = regex.matcher(removeList);
        String res = "-666";
        while (m.find()) {
            res += "," + m.group();
        }
        return res;
    }

    void initComponents() {
        // super.setTitle("GUI FTPReport ver 0.1.7");
        setSize(1050, 550);

        //creating table
        tbl = new CustomTable(this.model);
        setTableColumnWidth(tbl);

        tbl.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                jTableMouseClicked(tbl, evt);
            }

            public void mouseReleased(java.awt.event.MouseEvent evt) {
                jTableMouseReleased(tbl, evt);
            }
        });
        scrollPane = new JScrollPane(tbl);
        //make right panel
        createRightPanel("FTP");

        /*опишем подсказки*/
        bMakeXLS.setToolTipText("Выгрузка в Excel может занять продолжительное время, "
                + "т.к. достаются логи по всем выбранным аптекам");
        bDoExchange.setToolTipText("Произвести обмен(s2w) по выбранной аптеке");
        countDaysTF.setToolTipText("Параметр передается в базу видом sysdate - n");
        removeListLbl.setToolTipText("Аптеки можно разделять запятыми, точками или переносом строки");
        removeList.setToolTipText("Аптеки можно разделять запятыми, точками или переносом строки");

        bGetData.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mouseGetData(evt);
            }
        });

        bMakeXLS.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                mouseMakeXLS(evt);
                JOptionPane.showMessageDialog(null, "Выгружено на диск С в файл unload.xls", "Done", 1);
            }
        });

        makeFrame.setSelected(true);
        txtOnMainFrame.setSize(50, 50);
        txtOnMainFrame.setRows(10);
        txtScrollOnMainFrame.setViewportView(txtOnMainFrame);
        add(txtScrollOnMainFrame, BorderLayout.PAGE_END);
        getContentPane().add(scrollPane);

        //do menuBar
        JMenu mainMenu = new JMenu("Выбор основания для отчета");
        JMenuItem menuItemSourceFTP = new JMenuItem("построить по данным с FTP");
        menuItemSourceFTP.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                createRightPanel("FTP");
            }
        });
        JMenuItem menuItemSourceDB = new JMenuItem("построить по данным из БД");
        menuItemSourceDB.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                createRightPanel("DB");
            }
        });
        mainMenu.add(menuItemSourceFTP);
        mainMenu.add(menuItemSourceDB);
        JMenu about = new JMenu("?");
        JMenuItem menuItemAbout = new JMenuItem("АБАУТ");
        menuItemAbout.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent e) {
                JOptionPane.showMessageDialog(null,
                        "Версия 0.1.4:\n"
                        + "  Появилась выгрузка в MS Excel\n"
                        + "  При формировании XLS файла достаются логи по всем показанным аптекам"
                        + "  (занимает некоторое время)\n"
                        + "\n"
                        + "Версия 0.1.5:\n"
                        + "  Добавились поля из базы: \n"
                        + "  lastRCVD_pack_DB - последняя прогруженная посылка\n"
                        + "  last_pack_date_DB - дата прогрузки этой посылки\n"
                        + "  при получении ошибки \"Connect DB error\" нажмите кнопку \"Получить данные\"\n"
                        + "\n"
                        + "Версия 0.1.6 beta:\n"
                        + "  Планируется возможность проводить обмен(s2w) по аптеке,\n"
                        + "  пока не подключена из-за нестабильного поведения\n"
                        + "  Доработан механизм подбора ширины столбцов таблицы\n"
                        + "  Добавлены расшифровки(помощь) для некоторых кнопок\\полей\n"
                        + "\n"
                        + "Версия 0.1.7: \n"
                        + "  Добавлены новые поля из БД, добвылены для совместимости доп. возможностей\n"
                        + "  Добавлена возможность формирования отчета по данным FTP или базы данных\n"
                        + "  Изменен принцип формирования правой панели, для добавления списка исключений\n"
                        + "  Список исключений содержит аптеки, не учавствующие в запросе при работе по БД\n"
                        + "  Увеличина общая производительность\n"
                        + "  добавлена русификация имен столбцов\n"
                        + "\n"
                        + "Версия 0.1.8: \n"
                        + "  добавлен сервис для получения лога с сервера\n"
                        + "", "АБАУТ", 3);
            }
        });
        about.add(menuItemAbout);
        JMenuBar menuBar = new JMenuBar();
        menuBar.add(mainMenu);
        menuBar.add(new JSeparator(1));
        menuBar.add(about);
        setJMenuBar(menuBar);

        super.add(rightPanel, BorderLayout.EAST);
        try {
            UIManager.setLookAndFeel(metal);
        } catch (Exception ex) {
            Logger.getLogger(FTPReportGUI.class.getName()).log(Level.SEVERE, null, ex);
        }
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setVisible(true);
    }
    /*
     пока так, нужно подумать, как лучше сделать настройку ширины
     */

    void setTableColumnWidth(CustomTable tbl) {
        //       MyTableModel mtm = (MyTableModel)tbl.getModel();
        //       String[] allCol = mtm.getColumnNames();
//        for (int i = 0; i < allCol.length; i++) {
//             tbl.getColumn(allCol[i]).equals("sd");
//                     
//                     .setMinWidth(colNames[i].length());
//        }
        tbl.setAutoResizeMode(CustomTable.AUTO_RESIZE_OFF);
        tbl.getColumnModel().getColumn(0).setMaxWidth(65);
        tbl.getColumnModel().getColumn(1).setMaxWidth(75);
        tbl.getColumnModel().getColumn(2).setMaxWidth(75);
        tbl.getColumnModel().getColumn(4).setMinWidth(120);
        tbl.getColumnModel().getColumn(8).setMinWidth(155);
        tbl.getColumnModel().getColumn(9).setMinWidth(120);
    }

    public void setDoByFTP(boolean doByFTP) {
        this.doByFTP = doByFTP;
    }

    public boolean getDoByFTP() {
        return this.doByFTP;
    }

    public void setTitle(String sTitle) {
        super.setTitle(sTitle);
    }
    private JTextArea txtOnMsgFrame = new JTextArea();
    private JScrollPane txtScrollOnMsgFrame = new JScrollPane();
    private JTextArea txtOnMainFrame = new JTextArea();
    private JScrollPane txtScrollOnMainFrame = new JScrollPane();
    private JCheckBox makeFrame = new JCheckBox("Показать log в отдельном окне");
    private MSGText msgFrame = new MSGText("Log is");
    private JTextField countDaysTF = new JTextField("2", 5);
    private JTextField countPacksTF = new JTextField("100", 5);
    private JButton bDoExchange = new JButton("***Не выбрана строка***");
    private CustomTable tbl = null;
    private JScrollPane scrollPane = null;
    final String windows = "com.sun.java.swing.plaf.windows.WindowsLookAndFeel";
    final String motif = "com.sun.java.swing.plaf.motif.MotifLookAndFeel";
    final String metal = "javax.swing.plaf.metal.MetalLookAndFeel";
    //doRightPanel
    private JPanel rightPanel = new JPanel(new GridLayout(10, 1));
    private JLabel countDaysLbl = new JLabel("Записи старше, дни");
    private JLabel countPacksLbl = new JLabel("Кол-во не прогруженных пакетов более");
    //private JButton about = new JButton("что нового?");
    private JLabel removeListLbl = new JLabel("Список исключаемых аптек:");
    JScrollPane jScrollRemoveList = new javax.swing.JScrollPane();
    private JTextArea removeList = new JTextArea("200136, \n"
            + "1600155,\n"
            + "5200009,\n"
            + "5200045,\n"
            + "5400229,\n"
            + "5920208,\n"
            + "5920234,\n"
            + "6620394,\n"
            + "7700106");
    private boolean doByFTP = true;
    private JButton bGetData = new JButton("Получить данные");
    private JButton bMakeXLS = new JButton("Выгрузить XLS");

    public static void main(String[] args) {
//        int okCxl = JOptionPane.showConfirmDialog(null, pf, "Введите пароль для доступа  к данным из БД", JOptionPane.OK_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE);
//        if (okCxl == JOptionPane.OK_OPTION) {
//            sPwd = new String(pf.getPassword());
//            System.err.println("You password is: " + sPwd);
//        }
        FTPReportGUI mainForm = new FTPReportGUI();

    }
}
