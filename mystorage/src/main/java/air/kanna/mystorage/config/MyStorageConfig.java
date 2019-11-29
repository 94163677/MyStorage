package air.kanna.mystorage.config;

import java.util.ArrayList;
import java.util.List;

public class MyStorageConfig {
    //默认每页显示的数据量
    public static final int DEFAULT_PAGE_SIZE = 50;
  //默认最大的备份数据文件份数
    public static final int DEFAULT_BACKUP_SIZE = 5;
    //默认最大的备份日期数
    public static final int DEFAULT_BACKUP_DAY = 5;
    
    /**
     * 系统相关的设置
     */
    //数据库存放路径
    private String dbPath = "DB";
    //备份存放路径
    private String backPath = "BACKUP";
    //数据库文件名称
    private String dbFileName = "MyStorage.db";
    
    
    /**
     * 用户输入相关的保存性设置
     */
    //默认搜索文件名称
    private String searchFileName = "";
  //搜索文件名称Or条件
    private String searchFileNameOr = "";
  //搜索文件名称Not条件
    private String searchFileNameNot = "";
    //搜索文件类型
    private String searchFileType = "";
    //搜索指定的磁盘
    private String searchDiskPath = "";
    //扫描磁盘的时候是否计算文件的hash值
    private String isScanWithHash = "false";
    
    
    /**
     * 用户相关的设置
     */
    //每页显示的数量
    private int pageSize = DEFAULT_PAGE_SIZE;
    //是否自动备份
    private String isAutoBackup = "false";
    //是否按日期备份
    private String isBackupByDate = "false";
    //最大备份数(如果按日期备份，则表示每个日期的最大备份数)
    private int maxBackupNum = DEFAULT_BACKUP_SIZE;
    //最大备份日期数
    private int maxBackupDay = DEFAULT_BACKUP_DAY;
    
    
    /**
     * 界面相关的设置
     */
    //JTable中每列的宽度
    private List<Integer> tableColumnWidth = new ArrayList<>();
    
    
    
    
    
    public String getDbPath() {
        return dbPath;
    }
    public void setDbPath(String dbPath) {
        this.dbPath = dbPath;
    }
    public String getBackPath() {
        return backPath;
    }
    public void setBackPath(String backPath) {
        this.backPath = backPath;
    }
    public String getDbFileName() {
        return dbFileName;
    }
    public void setDbFileName(String dbFileName) {
        this.dbFileName = dbFileName;
    }
    public String getSearchFileName() {
        return searchFileName;
    }
    public void setSearchFileName(String searchFileName) {
        this.searchFileName = searchFileName;
    }
    public String getSearchFileNameOr() {
        return searchFileNameOr;
    }
    public String getSearchFileNameNot() {
        return searchFileNameNot;
    }
    public void setSearchFileNameOr(String searchFileNameOr) {
        this.searchFileNameOr = searchFileNameOr;
    }
    public void setSearchFileNameNot(String searchFileNameNot) {
        this.searchFileNameNot = searchFileNameNot;
    }
    public String getSearchFileType() {
        return searchFileType;
    }
    public void setSearchFileType(String searchFileType) {
        this.searchFileType = searchFileType;
    }
    public String getSearchDiskPath() {
        return searchDiskPath;
    }
    public void setSearchDiskPath(String searchDiskPath) {
        this.searchDiskPath = searchDiskPath;
    }
    public int getPageSize() {
        return pageSize;
    }
    public void setPageSize(int pageSize) {
        this.pageSize = pageSize;
    }
    public List<Integer> getTableColumnWidth() {
        return tableColumnWidth;
    }
    public void setTableColumnWidth(List<Integer> tableColumnWidth) {
        this.tableColumnWidth = tableColumnWidth;
    }
    public String getIsScanWithHash() {
        return isScanWithHash;
    }
    public void setIsScanWithHash(String isScanWithHash) {
        this.isScanWithHash = isScanWithHash;
    }
    public String getIsBackupByDate() {
        return isBackupByDate;
    }
    public void setIsBackupByDate(String isBackupByDate) {
        this.isBackupByDate = isBackupByDate;
    }
    public int getMaxBackupNum() {
        return maxBackupNum;
    }
    public void setMaxBackupNum(int maxBackupNum) {
        this.maxBackupNum = maxBackupNum;
    }
    public int getMaxBackupDay() {
        return maxBackupDay;
    }
    public void setMaxBackupDay(int maxBackupDay) {
        this.maxBackupDay = maxBackupDay;
    }
    public String getIsAutoBackup() {
        return isAutoBackup;
    }
    public void setIsAutoBackup(String isAutoBackup) {
        this.isAutoBackup = isAutoBackup;
    }
}
