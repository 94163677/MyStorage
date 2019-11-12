package air.kanna.mystorage;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridBagConstraints;
import java.awt.GridBagLayout;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import air.kanna.kindlesync.scan.FileScanner;
import air.kanna.kindlesync.scan.PathScanner;
import air.kanna.kindlesync.util.Nullable;
import air.kanna.mystorage.config.MyStorageConfig;
import air.kanna.mystorage.config.MyStorageConfigService;
import air.kanna.mystorage.config.impl.MyStorageConfigServicePropertiesImpl;
import air.kanna.mystorage.dao.DiskDescriptionDAO;
import air.kanna.mystorage.dao.FileItemDAO;
import air.kanna.mystorage.dao.OrderBy;
import air.kanna.mystorage.dao.Pager;
import air.kanna.mystorage.dao.condition.FileItemCondition;
import air.kanna.mystorage.dao.impl.sqlite.DiskDescriptionDAOSqliteImpl;
import air.kanna.mystorage.dao.impl.sqlite.FileItemDAOSqliteImpl;
import air.kanna.mystorage.dao.impl.sqlite.init.SqliteInitialize;
import air.kanna.mystorage.model.DiskDescription;
import air.kanna.mystorage.model.FileItem;
import air.kanna.mystorage.model.FileType;
import air.kanna.mystorage.service.DiskDescriptionService;
import air.kanna.mystorage.service.FileItemService;
import air.kanna.mystorage.service.SourceFileItemGetter;
import air.kanna.mystorage.service.impl.DiskDescriptionServiceImpl;
import air.kanna.mystorage.service.impl.FileItemServiceImpl;
import air.kanna.mystorage.service.impl.LocalSourceFileItemGetter;
import air.kanna.mystorage.util.StringUtil;

public class StartUp {
    private static final Logger logger = Logger.getLogger(StartUp.class);
    private static final String TITLE = "磁盘离线搜索工具";
    private static final String CONFIG_FILE = "config.cfg";
    
    private JFrame frame;
    private JTable dataTable;
    private JLabel processTextTb;
    private JLabel pageNumLb;
    private JLabel totalPageLb;
    private JLabel totalItemLb;
    private JProgressBar executeProcess;
    private JTextField fileNameTf;
    private JComboBox<String> fileTypeCb;
    private JComboBox<String> diskCb;
    private JButton newDiskBtn;
    private JButton delDiskBtn;
    private JButton rescanBtn;
    private JButton resetBtn;
    private JButton searchBtn;
    private JButton prevBtn;
    private JButton nextBtn;
    private JButton settingBtn;
    
    private MyStorageConfig config;
    private NewDiskDialog newDiskDialog;
    private List<DiskDescription> diskList;
    
    private MyStorageConfigService configService;
    private FileItemService itemService;
    private DiskDescriptionService diskService;
    private SourceFileItemGetter getter;
    
    private OrderBy order;
    private Pager pager;

    private TableColumnModelListener colummModelListener;
    private ProcessAndLabelProcListener processListener;
    private int[] columnLength = new int[] {0, 0, 0, 0, 0, 0, 0};
    

    /**
     * Create the application.
     */
    public StartUp() {
        initialize();
        initControl();
        initData();
    }
    
    private void setWaiting(boolean isWaiting) {
        boolean enable = !isWaiting;
        
        fileNameTf.setEnabled(enable);
        fileTypeCb.setEnabled(enable);
        diskCb.setEnabled(enable);
        newDiskBtn.setEnabled(enable);
        delDiskBtn.setEnabled(enable);
        rescanBtn.setEnabled(enable);
        resetBtn.setEnabled(enable);
        searchBtn.setEnabled(enable);
        if(enable) {
            resetPage();
        }else {
            prevBtn.setEnabled(enable);
            nextBtn.setEnabled(enable);
        }
    }
    
    private void initControl() {
        frame.addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosing(WindowEvent e) {
                saveToConfig();
                if(!configService.saveConfig(config)) {
                    logger.error("Save Config to file error");
                }
                System.exit(0);
            }
        });
        
        resetBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                fileNameTf.setText("");
                fileTypeCb.setSelectedIndex(0);
                diskCb.setSelectedIndex(0);
            }
        });
        
        newDiskBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                newDiskDialog.setModal(true);
                newDiskDialog.setVisible(true);
                DiskDescription disk = newDiskDialog.getDisk();
                if(disk == null) {
                    return;
                }
                for(DiskDescription scaned : diskList) {
                    if(disk.getBasePath().equalsIgnoreCase(scaned.getBasePath())) {
                        JOptionPane.showMessageDialog(frame, "重复的磁盘目录", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                    if(disk.getBasePath().contains(scaned.getBasePath())
                            || scaned.getBasePath().contains(disk.getBasePath())) {
                        JOptionPane.showMessageDialog(frame, "有重叠的磁盘目录", "错误", JOptionPane.ERROR_MESSAGE);
                        return;
                    }
                }
                if(diskService.add(disk) <= 0) {
                    JOptionPane.showMessageDialog(frame, "保存磁盘目录失败，详情请查看日志", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                reFlushDiskList();
                reScanDisk(disk);
                
            }
        });
        
        rescanBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int index = diskCb.getSelectedIndex();
                if(index <= 0 || index > diskList.size()) {
                    JOptionPane.showMessageDialog(frame, "请选择需要重新扫描的磁盘", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                reScanDisk(diskList.get(index - 1));
            }
        });
        
        delDiskBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int index = diskCb.getSelectedIndex();
                if(index <= 0 || index > diskList.size()) {
                    JOptionPane.showMessageDialog(frame, "请选择需要删除的磁盘", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                deleteDisk(diskList.get(index - 1));
                reFlushDiskList();
            }
        });
        
        searchBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                order = getDefaultOrder();
                pager.setPage(1);
                pager.setTotal(-1);
                doSearch();
            }
        });
        
        prevBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                if(pager.getPage() <= 1) {
                    JOptionPane.showMessageDialog(frame, "已经是第一页", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                pager.setPage(pager.getPage() - 1);
                doSearch();
            }
        });
        
        nextBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int total = 1 + (int)(pager.getTotal() / pager.getSize());
                if(pager.getPage() >= total) {
                    JOptionPane.showMessageDialog(frame, "已经是最后一页", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                pager.setPage(pager.getPage() + 1);
                doSearch();
            }
        });
        
        colummModelListener = new TableColumnModelListener() {

            @Override
            public void columnAdded(TableColumnModelEvent e) {}

            @Override
            public void columnMarginChanged(ChangeEvent event) {
                Object obj = event.getSource();
                if(!(obj instanceof TableColumnModel)) {
                    return;
                }
                TableColumnModel tableColumnModel = (TableColumnModel)obj;
                for(int i=0; i<tableColumnModel.getColumnCount(); i++) {
                    TableColumn column = tableColumnModel.getColumn(i);
                    columnLength[i] = column.getWidth();
                }
            }

            @Override
            public void columnMoved(TableColumnModelEvent e) {}

            @Override
            public void columnRemoved(TableColumnModelEvent e) {}

            @Override
            public void columnSelectionChanged(ListSelectionEvent e) {}
            
        };
        
        processListener = new ProcessAndLabelProcListener(processTextTb, executeProcess);

        dataTable.getColumnModel().addColumnModelListener(colummModelListener);
    }
    
    private void deleteDisk(DiskDescription disk) {
        if(disk == null) {
            return;
        }
        int idx = getDiskIndex(disk);
        if(idx < 0) {
            JOptionPane.showMessageDialog(frame, "请选择要重新扫描的磁盘", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        FileItemCondition condition = new FileItemCondition();
        int delCount = 0;
        
        disk = diskList.get(idx);
        condition.setDiskId(disk.getId());
        
        try {
            delCount = diskService.deleteById(disk.getId());
            if(delCount <= 0) {
                JOptionPane.showMessageDialog(frame, "删除磁盘(" + disk.getId() + ")失败，详情请看日志", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }catch(Exception e) {
            logger.error("Delete FileItem by Disk Error", e);
            JOptionPane.showMessageDialog(frame, "删除磁盘(" + disk.getId() + ")失败，详情请看日志", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        delCount = 0;
        try {
            delCount = itemService.deleteByCondition(condition);
        }catch(Exception e) {
            logger.error("Delete FileItem by Disk Error", e);
            JOptionPane.showMessageDialog(frame, "删除原来磁盘数据(" + disk.getId() + ")错误，详情请看日志", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(frame, 
                "删除磁盘(" + disk.getBasePath() + ")成功，删除原来" + delCount + "条数据。",
                "信息", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void reScanDisk(DiskDescription disk) {
        if(disk == null) {
            return;
        }
        int idx = getDiskIndex(disk);
        if(idx < 0) {
            JOptionPane.showMessageDialog(frame, "请选择要重新扫描的磁盘", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        setWaiting(true);
        
        new Thread() {
            @Override
            public void run() {
                FileItemCondition condition = new FileItemCondition();
                List<FileItem> items = null;
                int insCount = 0, delCount = 0;

                DiskDescription disk = diskList.get(idx);
                condition.setDiskId(disk.getId());
                processListener.setMax(1000);
                
                try {
                    processListener.setPosition(100, "开始扫描目录：" + disk.getBasePath());
                    items = getter.createNewDiskFileItem(disk);
                }catch(Exception e) {
                    logger.error("Scan Disk Error", e);
                    JOptionPane.showMessageDialog(frame, "扫描磁盘(" + disk.getBasePath() + ")错误，详情请看日志", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                try {
                    processListener.setPosition(200, "删除原目录数据");
                    delCount = itemService.deleteByCondition(condition);
                }catch(Exception e) {
                    logger.error("Delete FileItem by Disk Error", e);
                    JOptionPane.showMessageDialog(frame, "删除原来磁盘数据(" + disk.getId() + ")错误，详情请看日志", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }
                
                long time1 = System.currentTimeMillis(), time2 = 0L, time3 = 0L, leftSecond = 0L;
                try {
                    if(items != null && items.size() > 0) {
                        int current = 0, total = items.size();
                        
                        time3 = time2 = time1;
                        
                        for(int i=0; i<total; i++) {
                            FileItem item = items.get(i);
                            current = (int)(i * 800 / total) + 200;
                            processListener.setPosition(current, 
                                    "处理中，预计剩下 " + getShowTimeBySecond(leftSecond)  + "：" + item.getFileName());
                            insCount += itemService.add(item);
                            time2 = System.currentTimeMillis();
                            leftSecond = (long)(((time2 - time1) * (total - i - 1)) / ((i + 1) * 1000));
                        }
                    }
                }catch(Exception e) {
                    logger.error("Insert FileItem by Disk Error", e);
                    JOptionPane.showMessageDialog(frame, "新增磁盘数据(" + disk.getId() + ")错误，详情请看日志", "错误", JOptionPane.ERROR_MESSAGE);
                    return;
                }finally {
                    setWaiting(false);
                    
                    processListener.finish("扫描完成，总耗时" + getShowTimeBySecond((System.currentTimeMillis() - time1) / 1000));
                }
                JOptionPane.showMessageDialog(frame, 
                        "重新扫描磁盘(" + disk.getBasePath() + ")成功，删除原来" + delCount + "条数据，新增" + insCount + "条数据。",
                        "信息", JOptionPane.INFORMATION_MESSAGE);
            }
        }.start();
    }
    
    private String getShowTimeBySecond(long second) {
        StringBuilder sb = new StringBuilder();
        if(second <= 0) {
            return "计算中";
        }
        if(second < 60) {
            return second + "秒";
        }
        
        int showSec = (int)(second % 60);
        second /= 60;
        if(second < 60) {
            sb.append(second).append("分").append(showSec).append("秒");
            return sb.toString();
        }
        
        int showMin = (int)(second % 60);
        second /= 60;
        sb.append(second).append("小时").append(showMin).append("分").append(showSec).append("秒");
        return sb.toString();
    }
    
    private void initData() {
        diskList = new ArrayList<>();
        
        File configFile = new File(CONFIG_FILE);
        logger.info("config file: " + configFile.getAbsolutePath());
        configService = new MyStorageConfigServicePropertiesImpl(configFile);
        getter = new LocalSourceFileItemGetter();
        order = getDefaultOrder();
        pager = new Pager();
        
        FileScanner scanner = new PathScanner();
        ((LocalSourceFileItemGetter)getter).setScanner(scanner);
        
        config = configService.getConfig();
        if(config == null) {
            config = new MyStorageConfig();
        }
        pager.setTotal(-1);
        pager.setSize(50);
        
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
                    .append(File.separator)
                    .append(config.getDbFileName())
                    .toString());
        
        initDB(tmpPath);
        reFlushDiskList();
        
        setFromConfig();
        resetTableColumnWidth(dataTable);
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
            ((FileItemServiceImpl)itemService).setModelDao(itemDao);
            
        }catch(Exception e) {
            logger.error("initDB error: ", e);
            JOptionPane.showMessageDialog(frame, "数据库加载失败，详情请查看日志", "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
    }
    
    private void reFlushDiskList() {
        DiskDescription selected = getSelectedDisk();
        try {
            diskList = diskService.listAll(null, null);
            if(diskList == null) {
                JOptionPane.showMessageDialog(frame, "磁盘数据加载失败，详情请查看日志", "错误", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
            Vector<String> disks = new Vector<>();
            disks.add("全部");
            for(DiskDescription disk : diskList) {
                disks.add(disk.getBasePath());
            }
            diskCb.setModel(new DefaultComboBoxModel<String>(disks));
        }catch(Exception e) {
            logger.error("get data error: ", e);
            JOptionPane.showMessageDialog(frame, "数据加载失败，详情请查看日志", "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        if(selected == null) {
            return;
        }
        int idx = getDiskIndex(selected);
        if(idx >= 0) {
            diskCb.setSelectedIndex(idx);
        }
    }
    
    private DiskDescription getSelectedDisk() {
        if(diskList == null || diskList.size() <= 0) {
            return null;
        }
        int index = diskCb.getSelectedIndex();
        if(index <= 0 || index > diskList.size()) {
            return null;
        }
        return diskList.get(index - 1);
    }
    
    private int getDiskIndex(DiskDescription disk) {
        if(diskList == null || disk == null || diskList.size() <= 0) {
            return -1;
        }
        for(int i=0; i<diskList.size(); i++) {
            DiskDescription diskItem = diskList.get(i);
            if(disk.equals(diskItem)) {
                return i;
            }
        }
        return -1;
    }
    
    private DiskDescription getDiskById(Long diskId) {
        if(diskList == null || diskList.size() <= 0 || diskId == null || diskId <= 0) {
            return null;
        }
        for(int i=0; i<diskList.size(); i++) {
            DiskDescription diskItem = diskList.get(i);
            if(diskItem.getId() == diskId.longValue()) {
                return diskItem;
            }
        }
        return null;
    }
    
    private void doSearch() {
        FileItemCondition condition = new FileItemCondition();
        
        if(StringUtil.isNotSpace(fileNameTf.getText())) {
            condition.setFileName(fileNameTf.getText());
        }
        if(fileTypeCb.getSelectedIndex() > 0) {
            if(fileTypeCb.getSelectedIndex() == 1) {
                condition.setFileType(FileType.TYPE_FILE.getType());
            }else
            if(fileTypeCb.getSelectedIndex() == 2) {
                condition.setFileType(FileType.TYPE_DICECTORY.getType());
            }
        }
        DiskDescription disk = getSelectedDisk();
        if(disk != null) {
            condition.setDiskId(disk.getId());
        }
        
        if(pager.getTotal() < 0) {
            pager.setTotal(itemService.getByConditionCount(condition));
        }
        if(pager.getTotal() > 0) {
            List<FileItem> itemList = itemService.getByCondition(condition, order, pager);
            dataTable.setModel(fileItemListToTableModel(itemList));
        }else {
            dataTable.setModel(getEmptyModel());
        }
        resetTableColumnWidth(dataTable);
        
        resetPage();
    }
    
    private void resetPage() {
        int total = 1 + (int)(pager.getTotal() / pager.getSize());
        
        pageNumLb.setText("" + pager.getPage());
        totalPageLb.setText("" + total);
        totalItemLb.setText("" + pager.getTotal());
        
        if(pager.getPage() <= 1) {
            prevBtn.setEnabled(false);
        }else {
            prevBtn.setEnabled(true);
        }
        if(pager.getPage() < total) {
            nextBtn.setEnabled(true);
        }else {
            nextBtn.setEnabled(false);
        }
    }
    
    private void resetTableColumnWidth(JTable table) {
        if(table == null) {
            return;
        }
        TableColumnModel columnModel = table.getColumnModel();
        
        if(columnModel == null || columnModel.getColumnCount() <= 0) {
            return;
        }
        columnModel.removeColumnModelListener(colummModelListener);
        for(int i=0; i<columnLength.length && i<columnModel.getColumnCount(); i++) {
            if(columnLength[i] <= 0) {
                continue;
            }
            columnModel.getColumn(i).setPreferredWidth(columnLength[i]);
        }
        columnModel.addColumnModelListener(colummModelListener);
    }
    
    private DefaultTableModel fileItemListToTableModel(List<FileItem> itemList) {
        DefaultTableModel model = getEmptyModel();
        if(itemList == null || itemList.size() <= 0) {
            return model;
        }
        for(FileItem item: itemList) {
            Vector<String> rows = new Vector<>();
            DiskDescription disk = getDiskById(item.getDiskId());
            
            rows.add(disk == null ? "NULL" : disk.getBasePath());
            rows.add(item.getFileName());
            rows.add(item.getFileTypeObj().getDescription());
            rows.add("" + item.getFileSize());
            rows.add(item.getFilePath());
            rows.add(item.getLastModDateStr());
            rows.add(item.getRemark());
            
            model.addRow(rows);
        }
        return model;
    }
    
    private DefaultTableModel getEmptyModel() {
        DefaultTableModel model = new DefaultTableModel();
        
        model.addColumn("磁盘路径");
        model.addColumn("文件名");
        model.addColumn("文件类型");
        model.addColumn("文件大小");
        model.addColumn("文件路径");
        model.addColumn("最后修改日期");
        model.addColumn("备注");
        
        return model;
    }
    
    private OrderBy getDefaultOrder() {
        OrderBy defOrder = new OrderBy();
        
        defOrder.addOrderAsc("disk_id");
        defOrder.addOrderAsc("file_name");
        
        return defOrder;
    }
    
    private void setFromConfig() {
        fileNameTf.setText(config.getSearchFileName());
        if(!Nullable.isNull(config.getSearchFileType())) {
            if("D".equalsIgnoreCase(config.getSearchFileType())) {
                fileTypeCb.setSelectedIndex(1);
            }else
            if("F".equalsIgnoreCase(config.getSearchFileType())) {
                fileTypeCb.setSelectedIndex(2);
            }
        }
        if(!Nullable.isNull(config.getSearchDiskPath())) {
            for(int i=0; i<diskList.size(); i++) {
                if(config.getSearchDiskPath().equals(diskList.get(i).getBasePath())) {
                    diskCb.setSelectedIndex(i + 1);
                    break;
                }
            }
        }
        pager.setSize(config.getPageSize());
        
        for(int i=0; i<config.getTableColumnWidth().size() && i<columnLength.length; i++) {
            columnLength[i] = config.getTableColumnWidth().get(i).intValue();
        }
    }
    
    private void saveToConfig() {
        config.setSearchFileName(fileNameTf.getText());
        switch(fileTypeCb.getSelectedIndex()) {
            case 1 : config.setSearchFileType("D");break;
            case 2 : config.setSearchFileType("F");break;
            default: config.setSearchFileType("");
        }
        if(diskCb.getSelectedIndex() > 0) {
            config.setSearchDiskPath((String)diskCb.getSelectedItem());
        }else {
            config.setSearchDiskPath("");
        }
        
        config.getTableColumnWidth().clear();
        for(int i=0; i<columnLength.length; i++) {
            config.getTableColumnWidth().add(columnLength[i]);
        }
    }

    /**
     * Initialize the contents of the frame.
     */
    private void initialize() {
        frame = new JFrame();
        frame.setTitle(TITLE);
        frame.setBounds(100, 100, 1024, 576);
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.DO_NOTHING_ON_CLOSE);
        frame.getContentPane().setLayout(new BorderLayout(0, 0));
        
        JPanel paramPanel = new JPanel();
        frame.getContentPane().add(paramPanel, BorderLayout.WEST);
        paramPanel.setLayout(new GridLayout(8, 1, 0, 0));
        
        
        JPanel panel08 = new JPanel();
        paramPanel.add(panel08);
        
        settingBtn = new JButton("设置");
        panel08.add(settingBtn);
        settingBtn.setEnabled(false);//TODO 设定暂未实现
        
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
        fileTypeCb = new JComboBox<String>();
        fileTypeCb.setModel(new DefaultComboBoxModel<String>(new String[] {"全部", "文件", "目录"}));
        panel02.add(fileTypeCb);
        
        
        JPanel panel03 = new JPanel();
        paramPanel.add(panel03);
        panel03.setLayout(new GridLayout(2, 1, 0, 0));
        JLabel label_2 = new JLabel("所属磁盘：");
        panel03.add(label_2);
        diskCb = new JComboBox<String>();
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
        JPanel panel09 = new JPanel();
        prevBtn = new JButton("上一页");
        nextBtn = new JButton("下一页");
        paramPanel.add(panel07);
        
        GridBagLayout gridBagLayout = new GridBagLayout();
        GridBagConstraints constraints = new GridBagConstraints();
        
        panel07.setLayout(gridBagLayout);
        
        constraints.fill = GridBagConstraints.BOTH;
        constraints.weightx = 1.0;
        constraints.weighty = 1.0;
        constraints.gridheight = 1;
        constraints.gridwidth = GridBagConstraints.REMAINDER;
        gridBagLayout.setConstraints(panel09, constraints);
        panel07.add(panel09);
        
        constraints.gridheight = 1;
        constraints.gridwidth = 1;
        gridBagLayout.setConstraints(prevBtn, constraints);
        
        panel07.add(prevBtn);
        gridBagLayout.setConstraints(nextBtn, constraints);
        panel07.add(nextBtn);
        
        JLabel label_3 = new JLabel("页数：");
        panel09.add(label_3);
        
        pageNumLb = new JLabel("-");
        panel09.add(pageNumLb);
        
        JLabel label_4 = new JLabel(" / ");
        panel09.add(label_4);
        
        totalPageLb = new JLabel("-");
        panel09.add(totalPageLb);
        
        JLabel label_6 = new JLabel(" ; ");
        panel09.add(label_6);
        
        JLabel label_7 = new JLabel("总条数：");
        panel09.add(label_7);
        
        totalItemLb = new JLabel("-");
        panel09.add(totalItemLb);

        prevBtn.setEnabled(false);
        nextBtn.setEnabled(false);
        
        
        JPanel dataPanel = new JPanel();
        frame.getContentPane().add(dataPanel, BorderLayout.CENTER);
        dataPanel.setLayout(new BorderLayout(0, 0));
        
        dataTable = new JTable();
        dataTable.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        dataTable.setAutoResizeMode(JTable.AUTO_RESIZE_OFF);
        dataPanel.add(new JScrollPane(dataTable), BorderLayout.CENTER);
        dataTable.setModel(getEmptyModel());
        
        processTextTb = new JLabel("--");
        processTextTb.setHorizontalAlignment(SwingConstants.LEFT);
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
