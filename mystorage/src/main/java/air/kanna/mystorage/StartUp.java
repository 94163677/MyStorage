package air.kanna.mystorage;

import java.awt.BorderLayout;
import java.awt.EventQueue;
import java.awt.GridLayout;
import java.awt.Toolkit;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.Transferable;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.security.MessageDigest;
import java.sql.Connection;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Vector;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JCheckBox;
import javax.swing.JComboBox;
import javax.swing.JFrame;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JProgressBar;
import javax.swing.JScrollPane;
import javax.swing.JSlider;
import javax.swing.JTable;
import javax.swing.JTextField;
import javax.swing.ListSelectionModel;
import javax.swing.SwingConstants;
import javax.swing.event.ChangeEvent;
import javax.swing.event.ChangeListener;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.TableColumnModelEvent;
import javax.swing.event.TableColumnModelListener;
import javax.swing.table.DefaultTableModel;
import javax.swing.table.TableColumn;
import javax.swing.table.TableColumnModel;

import org.apache.log4j.Logger;

import air.kanna.kindlesync.compare.ListComparer;
import air.kanna.kindlesync.compare.OperationItem;
import air.kanna.kindlesync.scan.FileScanner;
import air.kanna.kindlesync.scan.PathScanner;
import air.kanna.kindlesync.util.CollectionUtil;
import air.kanna.kindlesync.util.Nullable;
import air.kanna.mystorage.backup.BackupService;
import air.kanna.mystorage.backup.impl.LocalBackupServiceImpl;
import air.kanna.mystorage.compare.FileItemListComparer;
import air.kanna.mystorage.config.MyStorageConfig;
import air.kanna.mystorage.config.MyStorageConfigService;
import air.kanna.mystorage.config.impl.MyStorageConfigServicePropertiesImpl;
import air.kanna.mystorage.dao.DiskDescriptionDAO;
import air.kanna.mystorage.dao.FileItemDAO;
import air.kanna.mystorage.dao.OrderBy;
import air.kanna.mystorage.dao.Pager;
import air.kanna.mystorage.dao.condition.FileItemCondition;
import air.kanna.mystorage.dao.condition.FileItemConditionExt;
import air.kanna.mystorage.dao.impl.sqlite.DiskDescriptionDAOSqliteImpl;
import air.kanna.mystorage.dao.impl.sqlite.FileItemDAOSqliteExtImpl;
import air.kanna.mystorage.dao.impl.sqlite.init.SqliteInitialize;
import air.kanna.mystorage.model.DiskDescription;
import air.kanna.mystorage.model.FileHash;
import air.kanna.mystorage.model.FileItem;
import air.kanna.mystorage.model.FileType;
import air.kanna.mystorage.service.DiskDescriptionService;
import air.kanna.mystorage.service.FileItemService;
import air.kanna.mystorage.service.SourceFileItemGetter;
import air.kanna.mystorage.service.hash.HashService;
import air.kanna.mystorage.service.hash.impl.LocalMsgDigestHashServiceImpl;
import air.kanna.mystorage.service.impl.DiskDescriptionServiceImpl;
import air.kanna.mystorage.service.impl.FileItemServiceImpl;
import air.kanna.mystorage.service.impl.LocalSourceFileItemGetter;
import air.kanna.mystorage.sync.SyncDBFileDialog;
import air.kanna.mystorage.util.StringUtil;

public class StartUp {
    private static final Logger logger = Logger.getLogger(StartUp.class);
    private static final String TITLE = "我的硬盘离线搜索工具";
    private static final String CONFIG_FILE = "config.cfg";
    
    private JFrame frame;
    private JTable dataTable;
    private JLabel processTextTb;
    private JLabel pageNumLb;
    private JLabel totalPageLb;
    private JLabel totalItemLb;
    private JProgressBar executeProcess;
    private JTextField fileNameTf;
    private JTextField fileNameOrTf;
    private JTextField fileNameNotTf;
    private JComboBox<String> fileTypeCb;
    private JComboBox<String> diskCb;
    private JCheckBox scanWithHashCb;
    private JButton newDiskBtn;
    private JButton delDiskBtn;
    private JButton rescanBtn;
    private JButton resetBtn;
    private JButton searchBtn;
    private JButton prevBtn;
    private JButton nextBtn;
    private JButton settingBtn;
    private JButton backupBtn;
    private JButton syncBtn;
    private JSlider pagerSlider;
    
    private MyStorageConfig config;
    private NewDiskDialog newDiskDialog;
    private SyncDBFileDialog syncDialog;
    private List<DiskDescription> diskList;
    
    private MyStorageConfigService configService;
    private FileItemService itemService;
    private DiskDescriptionService diskService;
    private SourceFileItemGetter getter;
    private HashService hashService;
    private BackupService backupService;
    private ListComparer<FileItem> comparer;
    
    private OrderBy order;
    private Pager pager;

    private TableColumnModelListener colummModelListener;
    private ProcessAndLabelProcListener processListener;
    private int[] columnLength = new int[] {0, 0, 0, 0, 0, 0, 0, 0, 0};
    
    private File dbFile;
    private File backupPath;
    
    public static void setSysClipboardText(String writeMe) {
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
        Transferable tText = new StringSelection(writeMe);
        clip.setContents(tText, null);
    }

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
        backupBtn.setEnabled(enable);
        syncBtn.setEnabled(enable);
        scanWithHashCb.setEnabled(enable);
        pagerSlider.setEnabled(enable);
        
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
                fileNameOrTf.setText("");
                fileNameNotTf.setText("");
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
                incrementScanDisk(disk);
                
            }
        });
        
        rescanBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                int index = diskCb.getSelectedIndex();
                if(index <= 0 || index > diskList.size()) {
                    JOptionPane.showMessageDialog(frame, "请选择需要重新扫描的磁盘", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                incrementScanDisk(diskList.get(index - 1));
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
                if(pager.getPage() >= pager.getMaxPage()) {
                    JOptionPane.showMessageDialog(frame, "已经是最后一页", "提示", JOptionPane.WARNING_MESSAGE);
                    return;
                }
                pager.setPage(pager.getPage() + 1);
                doSearch();
            }
        });
        
        backupBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                backupData();
            }
        });
        
        syncBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                setWaiting(true);
                try {
                    syncDialog = new SyncDBFileDialog(dbFile, frame);
                    syncDialog.setModal(true);
                    syncDialog.setVisible(true);
                }catch(RuntimeException e) {
                    JOptionPane.showMessageDialog(frame, e.getMessage(), "错误", JOptionPane.ERROR_MESSAGE);
                }catch(Exception e) {
                    JOptionPane.showMessageDialog(frame, "同步准备出错，详情请查看日志", "错误", JOptionPane.ERROR_MESSAGE);
                }finally {
                    setWaiting(false);
                }
            }
        });
        
        pagerSlider.addChangeListener(new ChangeListener() {
            public void stateChanged(ChangeEvent arg0) {
                pageNumLb.setText("" + pagerSlider.getValue());
            }
        });

        pagerSlider.addMouseWheelListener(new MouseWheelListener() {
            public void mouseWheelMoved(MouseWheelEvent event) {
                if(pager.getTotal() <= 0) {
                    return;
                }
                if(event.getWheelRotation() < 0) {
                    if(pager.getPage() <= 1) {
                        return;
                    }
                    pager.setPage(pager.getPage() - 1);
                    doSearch();
                }else
                if(event.getWheelRotation() > 0) {
                    if(pagerSlider.getValue() >= pagerSlider.getMaximum()) {
                        return;
                    }
                    pager.setPage(pager.getPage() + 1);
                    doSearch();
                    
                }
                
            }
        });
        
        pagerSlider.addMouseListener(new MouseAdapter() {
            public void mouseReleased(MouseEvent event) {
                if(pager.getTotal() > 0) {
                    pager.setPage(pagerSlider.getValue());
                    doSearch();
                }
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
                JOptionPane.showMessageDialog(frame, "删除磁盘(" + disk.getId() + ")失败，详情请查看日志", "错误", JOptionPane.ERROR_MESSAGE);
                return;
            }
        }catch(Exception e) {
            logger.error("Delete FileItem by Disk Error", e);
            JOptionPane.showMessageDialog(frame, "删除磁盘(" + disk.getId() + ")失败，详情请查看日志", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        delCount = 0;
        try {
            delCount = itemService.deleteByCondition(condition);
        }catch(Exception e) {
            logger.error("Delete FileItem by Disk Error", e);
            JOptionPane.showMessageDialog(frame, "删除原来磁盘数据(" + disk.getId() + ")错误，详情请查看日志", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        JOptionPane.showMessageDialog(frame, 
                "删除磁盘(" + disk.getBasePath() + ")成功，删除原来" + delCount + "条数据。",
                "信息", JOptionPane.INFORMATION_MESSAGE);
    }
    
    private void incrementScanDisk(DiskDescription disk) {
        if(disk == null) {
            return;
        }
        int idx = getDiskIndex(disk);
        if(idx < 0) {
            JOptionPane.showMessageDialog(frame, "请选择要增量扫描的磁盘", "错误", JOptionPane.ERROR_MESSAGE);
            return;
        }
        
        setWaiting(true);
        
        new Thread() {
            @Override
            public void run() {
                FileItemCondition condition = new FileItemCondition();
                List<FileItem> scanItems = null, dbItems = null;
                int insCount = 0, delCount = 0;
                boolean isHash = scanWithHashCb.isSelected();

                DiskDescription disk = diskList.get(idx);
                condition.setDiskId(disk.getId());
                processListener.setMax(1000);
                
                //扫描当前选择目录，获取最新文件列表
                try {
                    processListener.setPosition(100, "开始扫描目录：" + disk.getBasePath());
                    scanItems = getter.createNewDiskFileItem(disk);
                }catch(Exception e) {
                    logger.error("Scan Disk Error", e);
                    JOptionPane.showMessageDialog(frame, "扫描磁盘(" + disk.getBasePath() + ")错误，详情请查看日志", "错误", JOptionPane.ERROR_MESSAGE);
                    setWaiting(false);
                    return;
                }
                //获取数据库中的文件列表
                try {
                    processListener.setPosition(200, "获取原目录数据");
                    dbItems = itemService.getByCondition(condition, null, null);
                }catch(Exception e) {
                    logger.error("Delete FileItem by Disk Error", e);
                    JOptionPane.showMessageDialog(frame, "删除原来磁盘数据(" + disk.getId() + ")错误，详情请查看日志", "错误", JOptionPane.ERROR_MESSAGE);
                    setWaiting(false);
                    return;
                }
                List<OperationItem<FileItem>> compList = comparer.getCompareResult(scanItems, dbItems);
                long current = 200, processedLen = 0, totalLen = 0;
                long beginTime = System.currentTimeMillis();
                long prevTime = beginTime, currTime = 0, leftSecond = 0;
                
                if(isHash) {
                    for(OperationItem<FileItem> operItem : compList) {
                        if(operItem.getItem() != null) {
                            totalLen += operItem.getItem().getFileSize();
                        }
                    }
                }else {
                    totalLen = compList.size();
                }
                
                List<OperationItem<FileItem>> errorList = new ArrayList<>();
                
                for(OperationItem<FileItem> operItem : compList) {
                    processListener.setPosition((int)current, 
                            "处理中，预计剩下 " + getShowTimeBySecond(leftSecond)  + "：" + operItem.getItem().getFileName());
                    try {
                        switch(operItem.getOperation()) {
                            case REP: {
                                if(itemService.deleteById(operItem.getOrgItem().getId()) <= 0) {
                                    errorList.add(operItem);
                                    logger.error("REP error: " + operItem.getOrgItem().getFileName());
                                }
                            };
                            case ADD: {
                                if(isHash) {
                                    fillFileItemHash(operItem.getItem());
                                }
                                if(itemService.add(operItem.getItem()) <= 0) {
                                    errorList.add(operItem);
                                    logger.error("ADD error: " + operItem.getItem().getFileName());
                                }
                            };break;
                            case DEL: {
                                if(itemService.deleteById(operItem.getItem().getId()) <= 0) {
                                    errorList.add(operItem);
                                    logger.error("DEL error: " + operItem.getItem().getFileName());
                                }
                            };break;
                        }
                    }catch(Exception e) {
                        logger.error("process FileItem Error: " + operItem.getItem().getFileName(), e);
                        errorList.add(operItem);
                    }
                    if(isHash) {
                        processedLen += operItem.getItem().getFileSize();
                    }else {
                        processedLen++;
                    }
                    
                    current = (long)((800 * processedLen) / totalLen) + 200;
                    currTime = System.currentTimeMillis();
                    leftSecond = (long)(((currTime - prevTime) * (totalLen - processedLen)) / (processedLen * 1000));
                }
                setErrorFileItemToClipboard(errorList);
                processListener.finish("处理完成");
                setWaiting(false);
            }
        }.start();
    }
    
    private void setErrorFileItemToClipboard(List<OperationItem<FileItem>> errorList) {
        if(CollectionUtil.isEmpty(errorList)) {
            return;
        }
        StringBuilder sb = new StringBuilder();
        for(OperationItem<FileItem> operItem : errorList) {
            sb.append(operItem.getOperation().getOperationCode());
            sb.append(" : ").append(operItem.getItem().getFileName());
            sb.append('\n');
        }
        setSysClipboardText(sb.toString());
        JOptionPane.showMessageDialog(frame, "更新部分成功，详情请查看日志，已将失败记录复制到剪切板", "更新部分错误", JOptionPane.WARNING_MESSAGE);
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
                boolean isHash = scanWithHashCb.isSelected();

                DiskDescription disk = diskList.get(idx);
                condition.setDiskId(disk.getId());
                processListener.setMax(1000);
                
                try {
                    processListener.setPosition(100, "开始扫描目录：" + disk.getBasePath());
                    items = getter.createNewDiskFileItem(disk);
                }catch(Exception e) {
                    logger.error("Scan Disk Error", e);
                    JOptionPane.showMessageDialog(frame, "扫描磁盘(" + disk.getBasePath() + ")错误，详情请查看日志", "错误", JOptionPane.ERROR_MESSAGE);
                    setWaiting(false);
                    return;
                }
                try {
                    processListener.setPosition(200, "删除原目录数据");
                    delCount = itemService.deleteByCondition(condition);
                }catch(Exception e) {
                    logger.error("Delete FileItem by Disk Error", e);
                    JOptionPane.showMessageDialog(frame, "删除原来磁盘数据(" + disk.getId() + ")错误，详情请查看日志", "错误", JOptionPane.ERROR_MESSAGE);
                    setWaiting(false);
                    return;
                }
                //TODO 如果要md5或者sha256的话，要改为按大小计算时间
                long time1 = System.currentTimeMillis(), time2 = 0L, time3 = 0L, leftSecond = 0L;
                try {
                    if(items != null && items.size() > 0) {
                        int current = 0, total = items.size();
                        
                        time3 = time2 = time1;
                        
                        for(int i=0; i<total; i++) {
                            FileItem item = items.get(i);
                            current = (int)(i * 800 / total) + 200;
                            
                            if(isHash) {
                                fillFileItemHash(item);
                            }
                            
                            processListener.setPosition(current, 
                                    "处理中，预计剩下 " + getShowTimeBySecond(leftSecond)  + "：" + item.getFileName());
                            insCount += itemService.add(item);
                            time2 = System.currentTimeMillis();
                            leftSecond = (long)(((time2 - time1) * (total - i - 1)) / ((i + 1) * 1000));
                        }
                    }
                }catch(Exception e) {
                    logger.error("Insert FileItem by Disk Error", e);
                    JOptionPane.showMessageDialog(frame, "新增磁盘数据(" + disk.getId() + ")错误，详情请查看日志", "错误", JOptionPane.ERROR_MESSAGE);
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
    
    private void fillFileItemHash(FileItem item) {
        try {
            setFileItemHash(
                    hashService.getFileItemHashString(item), item);
        }catch(Exception e) {
            logger.warn("Fill hash error.", e);
        }
    }
    
    private void setFileItemHash(Map<String, String> hash, FileItem item) {
        if(hash == null || item == null || hash.size() <= 0) {
            return;
        }
        String hashStr = hash.get(FileHash.MD5.getValue());
        if(StringUtil.isNotSpace(hashStr)) {
            item.setFileHash01(hashStr);
        }
        
        hashStr = hash.get(FileHash.SHA256.getValue());
        if(StringUtil.isNotSpace(hashStr)) {
            item.setFileHash02(hashStr);
        }
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
        hashService = new LocalMsgDigestHashServiceImpl();
        backupService = new LocalBackupServiceImpl();
        comparer = new FileItemListComparer();
        
        try {
            for(int i=0; i<FileHash.values().length; i++) {
                ((LocalMsgDigestHashServiceImpl)hashService)
                    .addMessageDigest(MessageDigest.getInstance(FileHash.values()[i].getValue()));
            }
        }catch(Exception e) {
            logger.warn("Get MessageDigest Error.", e);
        }
        
        FileScanner scanner = new PathScanner();
        ((LocalSourceFileItemGetter)getter).setScanner(scanner);
        
        config = configService.getConfig();
        if(config == null) {
            config = new MyStorageConfig();
        }
        pager.setTotal(-1);
        pager.setSize(50);
        ((LocalBackupServiceImpl)backupService).setBackupByDate("true".equalsIgnoreCase(config.getIsBackupByDate()));
        ((LocalBackupServiceImpl)backupService).setMaxBackupDay(config.getMaxBackupDay());
        ((LocalBackupServiceImpl)backupService).setMaxBackupNum(config.getMaxBackupNum());
        
        
        File tmpPath = new File(config.getDbPath());
        backupPath = new File(config.getBackPath());
        
        checkAndCreatePath(tmpPath, "数据库目录");
        checkAndCreatePath(backupPath, "备份目录");
        
        dbFile = new File(
                new StringBuilder().append(tmpPath.getAbsolutePath())
                    .append(File.separator)
                    .append(config.getDbFileName())
                    .toString());
        
        initDB(dbFile);
        reFlushDiskList();
        
        setFromConfig();
        resetTableColumnWidth(dataTable);
        
        if("true".equalsIgnoreCase(config.getIsAutoBackup())) {
            backupData();
        }
    }
    
    private void checkAndCreatePath(File path, String showMsg) {
        if(path.exists() && path.isFile()) {
            JOptionPane.showMessageDialog(
                    frame, (showMsg + "不是目录：" + path.getAbsolutePath()), "错误", JOptionPane.ERROR_MESSAGE);
            System.exit(1);
        }
        if(!path.exists()) {
            if(!path.mkdirs()) {
                JOptionPane.showMessageDialog(
                        frame, (showMsg + "创建失败：" + path.getAbsolutePath()), "错误", JOptionPane.ERROR_MESSAGE);
                System.exit(1);
            }
        }
    }
    
    private void initDB(File dbFile) {
        SqliteInitialize dbInit = new SqliteInitialize();
        try {
            Connection conn = dbInit.initAndGetConnection(dbFile);
            
            DiskDescriptionDAO diskDao = new DiskDescriptionDAOSqliteImpl(conn);
            FileItemDAO itemDao = new FileItemDAOSqliteExtImpl(conn);
            
            itemService = new FileItemServiceImpl();
            diskService = new DiskDescriptionServiceImpl();
            
            ((DiskDescriptionServiceImpl)diskService).setModelDao((DiskDescriptionDAO)diskDao);
            ((FileItemServiceImpl)itemService).setModelDao(itemDao);
            ((FileItemServiceImpl)itemService).setDiskService(diskService);
            
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
    
    private List<String> getUnSpaceKeys(String key){
        if(StringUtil.isSpace(key)) {
            return new ArrayList<>(1);
        }
        String[] keys = key.split(" ");
        if(keys == null || keys.length <= 0) {
            keys = new String[] {key};
        }
        List<String> list = new ArrayList<>(keys.length);
        for(int i=0; i<keys.length; i++) {
            if(StringUtil.isSpace(keys[i])) {
                continue;
            }
            list.add(keys[i]);
        }
        return list;
    }
    
    private void doSearch() {
        FileItemConditionExt condition = new FileItemConditionExt();
        
        if(StringUtil.isNotSpace(fileNameTf.getText())) {
            condition.setFileNameIncludeAll(getUnSpaceKeys(fileNameTf.getText()));
        }
        if(StringUtil.isNotSpace(fileNameOrTf.getText())) {
            condition.setFileNameIncludeOne(getUnSpaceKeys(fileNameOrTf.getText()));
        }
        if(StringUtil.isNotSpace(fileNameNotTf.getText())) {
            condition.setFileNameExclude(getUnSpaceKeys(fileNameNotTf.getText()));
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
        int total = pager.getMaxPage();
        
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

        pagerSlider.setMinimum(1);
        pagerSlider.setMaximum(total);
        pagerSlider.setValue(pager.getPage());
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
            DiskDescription disk = item.getDiskDescription();
            
            if(disk == null) {
                disk = getDiskById(item.getDiskId());
            }
            
            rows.add(disk == null ? "NULL" : disk.getBasePath());
            rows.add(item.getFileName());
            rows.add(item.getFileTypeObj().getDescription());
            rows.add("" + item.getFileSize());
            rows.add(item.getFilePath());
            rows.add(item.getLastModDateStr());
            rows.add(item.getFileHash01() == null ? "" : item.getFileHash01());
            rows.add(item.getFileHash02() == null ? "" : item.getFileHash02());
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
        model.addColumn("MD5");
        model.addColumn("SHA256");
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
        fileNameOrTf.setText(config.getSearchFileNameOr());
        fileNameNotTf.setText(config.getSearchFileNameNot());
        
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
        
        if("true".equalsIgnoreCase(config.getIsScanWithHash())) {
            scanWithHashCb.setSelected(true);
        }else {
            scanWithHashCb.setSelected(false);
        }
        
        for(int i=0; i<config.getTableColumnWidth().size() && i<columnLength.length; i++) {
            columnLength[i] = config.getTableColumnWidth().get(i).intValue();
        }
    }
    
    private void saveToConfig() {
        config.setSearchFileName(fileNameTf.getText());
        config.setSearchFileNameOr(fileNameOrTf.getText());
        config.setSearchFileNameNot(fileNameNotTf.getText());
        
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
        
        if(scanWithHashCb.isSelected()) {
            config.setIsScanWithHash("true");
        }else {
            config.setIsScanWithHash("false");
        }
        
        config.getTableColumnWidth().clear();
        for(int i=0; i<columnLength.length; i++) {
            config.getTableColumnWidth().add(columnLength[i]);
        }
    }
    
    private void backupData() {
        setWaiting(true);
        String str = processTextTb.getText();
        processTextTb.setText("正在备份...");
        try {
            if(backupService.backup(dbFile, backupPath)) {
                JOptionPane.showMessageDialog(frame, 
                        "备份数据成功。",
                        "信息", JOptionPane.INFORMATION_MESSAGE);
            }else {
                JOptionPane.showMessageDialog(frame, 
                        "备份数据失败，详情请查看日志", 
                        "错误", JOptionPane.ERROR_MESSAGE);
            }
        }catch(Exception e) {
            logger.error("backup error", e);
            JOptionPane.showMessageDialog(frame, "备份数据失败，详情请查看日志", "错误", JOptionPane.ERROR_MESSAGE);
        }finally {
            processTextTb.setText(str);
            setWaiting(false);
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
        paramPanel.setLayout(new GridLayout(18, 1, 0, 0));
        
        
        JPanel panel08 = new JPanel();
        paramPanel.add(panel08);
        panel08.setLayout(new GridLayout(1, 3, 0, 0));

        settingBtn = new JButton("设置");
        backupBtn = new JButton("备份");
        syncBtn = new JButton("同步");

        panel08.add(settingBtn);
        panel08.add(backupBtn);
        panel08.add(syncBtn);

        
        settingBtn.setEnabled(false);//TODO 设定暂未实现
        
        JPanel panel01 = new JPanel();
        paramPanel.add(panel01);
        panel01.setLayout(new GridLayout(1, 1, 0, 0));
        JLabel label = new JLabel("文件名称（包含全部）：");
        panel01.add(label);
        
        JPanel panel12 = new JPanel();
        paramPanel.add(panel12);
        panel12.setLayout(new GridLayout(1, 1, 0, 0));
        fileNameTf = new JTextField();
        panel12.add(fileNameTf);
        fileNameTf.setColumns(15);
        
        JPanel panel13 = new JPanel();
        paramPanel.add(panel13);
        panel13.setLayout(new GridLayout(1, 1, 0, 0));
        JLabel label01 = new JLabel("文件名称（包含某个）：");
        panel13.add(label01);
        
        JPanel panel14 = new JPanel();
        paramPanel.add(panel14);
        panel14.setLayout(new GridLayout(1, 1, 0, 0));
        fileNameOrTf = new JTextField();
        panel14.add(fileNameOrTf);
        fileNameOrTf.setColumns(15);
        
        JPanel panel15 = new JPanel();
        paramPanel.add(panel15);
        panel15.setLayout(new GridLayout(1, 1, 0, 0));
        JLabel label02 = new JLabel("文件名称（不包含）：");
        panel15.add(label02);
        
        JPanel panel16 = new JPanel();
        paramPanel.add(panel16);
        panel16.setLayout(new GridLayout(1, 1, 0, 0));
        fileNameNotTf = new JTextField();
        panel16.add(fileNameNotTf);
        fileNameNotTf.setColumns(15);
        
        
        JPanel panel02 = new JPanel();
        paramPanel.add(panel02);
        panel02.setLayout(new GridLayout(1, 1, 0, 0));
        JLabel label_1 = new JLabel("文件类型：");
        panel02.add(label_1);
        
        JPanel panel17 = new JPanel();
        paramPanel.add(panel17);
        panel17.setLayout(new GridLayout(1, 1, 0, 0));
        fileTypeCb = new JComboBox<String>();
        fileTypeCb.setModel(new DefaultComboBoxModel<String>(new String[] {"全部", "文件", "目录"}));
        panel17.add(fileTypeCb);
        
        
        JPanel panel03 = new JPanel();
        paramPanel.add(panel03);
        panel03.setLayout(new GridLayout(1, 1, 0, 0));
        JLabel label_2 = new JLabel("所属磁盘：");
        panel03.add(label_2);
        
        JPanel panel18 = new JPanel();
        paramPanel.add(panel18);
        panel18.setLayout(new GridLayout(1, 1, 0, 0));
        diskCb = new JComboBox<String>();
        panel18.add(diskCb);
        
        
        JPanel panel04 = new JPanel();
        paramPanel.add(panel04);

        panel04.setLayout(new GridLayout(1, 3, 0, 0));
        newDiskBtn = new JButton("新增磁盘");
        panel04.add(newDiskBtn);
        delDiskBtn = new JButton("删除磁盘");
        panel04.add(delDiskBtn);
        rescanBtn = new JButton("重新扫描");
        panel04.add(rescanBtn);
        
        
        JPanel panel05 = new JPanel();
        paramPanel.add(panel05);
        panel05.setLayout(new GridLayout(2, 2, 0, 0));
        scanWithHashCb = new JCheckBox("扫描磁盘同时计算Hash");
        panel05.add(scanWithHashCb);
        
        JPanel panel20 = new JPanel();
        paramPanel.add(panel20);
        
        JPanel panel06 = new JPanel();
        paramPanel.add(panel06);
        panel06.setLayout(new GridLayout(1, 2, 0, 0));
        resetBtn = new JButton("重置条件");
        panel06.add(resetBtn);
        searchBtn = new JButton("开始搜索");
        panel06.add(searchBtn);
        
        
        JPanel panel07 = new JPanel();
        paramPanel.add(panel07);
        panel07.setLayout(new GridLayout(1, 1, 0, 0));
        
        JPanel panel10 = new JPanel();
        panel07.add(panel10);
        
        JLabel label_3 = new JLabel("页数：");
        panel10.add(label_3);
        
        pageNumLb = new JLabel("-");
        panel10.add(pageNumLb);
        
        JLabel label_4 = new JLabel(" / ");
        panel10.add(label_4);
        
        totalPageLb = new JLabel("-");
        panel10.add(totalPageLb);
        
        JLabel label_6 = new JLabel(" ; ");
        panel10.add(label_6);
        
        JLabel label_7 = new JLabel("总条数：");
        panel10.add(label_7);
        
        totalItemLb = new JLabel("-");
        panel10.add(totalItemLb);
        
        JPanel panel19 = new JPanel();
        paramPanel.add(panel19);
        JPanel panel11 = new JPanel();
        panel19.add(panel11);
        
        pagerSlider = new JSlider();
        pagerSlider.setMinimum(1);
        pagerSlider.setMaximum(1);
        pagerSlider.setValue(1);
        panel11.add(pagerSlider);
        
        JPanel panel09 = new JPanel();
        paramPanel.add(panel09);
        panel09.setLayout(new GridLayout(1, 2, 0, 0));
        prevBtn = new JButton("上一页");
        panel09.add(prevBtn);
        nextBtn = new JButton("下一页");
        panel09.add(nextBtn);
        
        prevBtn.setEnabled(false);
        nextBtn.setEnabled(false);
        
        
        JPanel dataPanel = new JPanel();
        frame.getContentPane().add(dataPanel, BorderLayout.CENTER);
        dataPanel.setLayout(new BorderLayout(0, 0));
        
        dataTable = new JTable() {
            @Override
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
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
