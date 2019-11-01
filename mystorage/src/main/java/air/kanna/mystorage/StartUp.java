package air.kanna.mystorage;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;

import org.apache.log4j.Logger;

import air.kanna.mystorage.config.MyStorageConfig;
import air.kanna.mystorage.config.MyStorageConfigService;
import air.kanna.mystorage.config.impl.MyStorageConfigServicePropertiesImpl;
import air.kanna.mystorage.dao.DiskDescriptionDAO;
import air.kanna.mystorage.dao.FileItemDAO;
import air.kanna.mystorage.dao.impl.sqlite.DiskDescriptionDAOSqliteImpl;
import air.kanna.mystorage.dao.impl.sqlite.FileItemDAOSqliteImpl;
import air.kanna.mystorage.dao.impl.sqlite.init.SqliteInitialize;
import air.kanna.mystorage.model.DiskDescription;
import air.kanna.mystorage.service.DiskDescriptionService;
import air.kanna.mystorage.service.FileItemService;
import air.kanna.mystorage.service.impl.DiskDescriptionServiceImpl;
import air.kanna.mystorage.service.impl.FileItemServiceImpl;

public class StartUp {
    private static final Logger logger = Logger.getLogger(StartUp.class);
    private static final String TITLE = "磁盘离线搜索工具";
    private static final String CONFIG_FILE = "config.cfg";
    
    private JFrame frame;
    private JTable dataTable;
    private JLabel processTextTb;
    private JProgressBar executeProcess;
    private JTextField fileNameTf;
    private JComboBox fileTypeCb;
    private JComboBox diskCb;
    private JButton newDiskBtn;
    private JButton delDiskBtn;
    private JButton rescanBtn;
    private JButton resetBtn;
    private JButton searchBtn;
    private JButton prevBtn;
    private JButton nextBtn;
    private JLabel label_3;
    private JLabel label_4;
    private JButton settingBtn;
    
    private MyStorageConfig config;
    private NewDiskDialog newDiskDialog;
    private List<DiskDescription> diskList;
    
    private MyStorageConfigService configService;
    private FileItemService itemService;
    private DiskDescriptionService diskService;
    

    /**
     * Create the application.
     */
    public StartUp() {
        initialize();
        initControl();
        initData();
    }
    
    private void initControl() {
        
    }
    
    private void initData() {
        diskList = new ArrayList<>();
        
        File configFile = new File(CONFIG_FILE);
        logger.info("config file: " + configFile.getAbsolutePath());
        configService = new MyStorageConfigServicePropertiesImpl(configFile);
        
        config = configService.getConfig();
        if(config == null) {
            config = new MyStorageConfig();
        }
        
        File tmpPath = new File(config.getDbPath());
        if(tmpPath.exists() && tmpPath.isFile()) {
            JOptionPane.showMessageDialog(frame, "数据库目录不是目录", "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        if(!tmpPath.exists()) {
            if(!tmpPath.mkdirs()) {
                JOptionPane.showMessageDialog(frame, "数据库目创建失败", "错误", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
        tmpPath = new File(
                new StringBuilder().append(tmpPath.getAbsolutePath())
                    .append(File.pathSeparator)
                    .append(config.getDbFileName())
                    .toString());
        
        initDB(tmpPath);
        
        
        setFromConfig();
    }
    
    private void initDB(File dbFile) {
        SqliteInitialize dbInit = new SqliteInitialize();
        try {
            Connection conn = dbInit.initAndGetConnection(dbFile);
            
            DiskDescriptionDAO diskDao = new DiskDescriptionDAOSqliteImpl(conn);
            FileItemDAO itemDao = new FileItemDAOSqliteImpl(conn);
            
            itemService = new FileItemServiceImpl();
            diskService = new DiskDescriptionServiceImpl();
            
            ((DiskDescriptionServiceImpl)diskService).setModelDao((DiskDescriptionDAO)diskDao);
            ((FileItemServiceImpl)itemService).setFileItemDao(itemDao);
            
        }catch(Exception e) {
            logger.error("initDB error: ", e);
            JOptionPane.showMessageDialog(frame, "数据库加载失败，详情请查看日志", "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void setFromConfig() {
        
    }
    
    private void saveToConfig() {
        
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setTitle(TITLE);
        frame.setBounds(100, 100, 1024, 576);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));
        
        JPanel paramPanel = new JPanel();
        frame.getContentPane().add(paramPanel, BorderLayout.WEST);
        paramPanel.setLayout(new GridLayout(8, 1, 0, 0));
        
        
        JPanel panel08 = new JPanel();
        paramPanel.add(panel08);
        
        settingBtn = new JButton("设置");
        panel08.add(settingBtn);
        
        JPanel panel01 = new JPanel();
        paramPanel.add(panel01);
        panel01.setLayout(new GridLayout(2, 1, 0, 0));
        JLabel label = new JLabel("文件名称：");
        panel01.add(label);
        fileNameTf = new JTextField();
        panel01.add(fileNameTf);
        fileNameTf.setColumns(15);
        
        
        JPanel panel02 = new JPanel();
        paramPanel.add(panel02);
        panel02.setLayout(new GridLayout(2, 1, 0, 0));
        JLabel label_1 = new JLabel("文件类型：");
        panel02.add(label_1);
        fileTypeCb = new JComboBox();
        fileTypeCb.setModel(new DefaultComboBoxModel(new String[] {"全部", "文件", "目录"}));
        panel02.add(fileTypeCb);
        
        
        JPanel panel03 = new JPanel();
        paramPanel.add(panel03);
        panel03.setLayout(new GridLayout(2, 1, 0, 0));
        JLabel label_2 = new JLabel("所属磁盘：");
        panel03.add(label_2);
        diskCb = new JComboBox();
        panel03.add(diskCb);
        
        
        JPanel panel04 = new JPanel();
        paramPanel.add(panel04);
        JPanel panel05 = new JPanel();
        
        
        paramPanel.add(panel05);
        panel05.setLayout(new GridLayout(1, 3, 0, 0));
        newDiskBtn = new JButton("新增磁盘");
        panel05.add(newDiskBtn);
        delDiskBtn = new JButton("删除磁盘");
        panel05.add(delDiskBtn);
        rescanBtn = new JButton("重新扫描");
        panel05.add(rescanBtn);
        
        
        JPanel panel06 = new JPanel();
        paramPanel.add(panel06);
        panel06.setLayout(new GridLayout(1, 2, 0, 0));
        resetBtn = new JButton("重置条件");
        panel06.add(resetBtn);
        searchBtn = new JButton("开始搜索");
        panel06.add(searchBtn);
        
        
        JPanel panel07 = new JPanel();
        paramPanel.add(panel07);
        panel07.setLayout(new GridLayout(2, 2, 0, 0));
        
        label_3 = new JLabel("当前页：");
        panel07.add(label_3);
        
        label_4 = new JLabel("总页数：");
        panel07.add(label_4);
        prevBtn = new JButton("上一页");
        panel07.add(prevBtn);
        nextBtn = new JButton("下一页");
        panel07.add(nextBtn);
        
        
        JPanel dataPanel = new JPanel();
        frame.getContentPane().add(dataPanel, BorderLayout.CENTER);
        dataPanel.setLayout(new BorderLayout(0, 0));
        
        dataTable = new JTable();
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataPanel.add(dataTable, BorderLayout.CENTER);
        
        processTextTb = new JLabel("--");
        processTextTb.setHorizontalAlignment(SwingConstants.CENTER);
        dataPanel.add(processTextTb, BorderLayout.NORTH);
        
        executeProcess = new JProgressBar();
        dataPanel.add(executeProcess, BorderLayout.SOUTH);
        
        newDiskDialog = new NewDiskDialog(frame);
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        EventQueue.invokeLater(new Runnable() {
            public void run() {
                try {
                    StartUp window = new StartUp();
                    window.frame.setVisible(true);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
    }
}
