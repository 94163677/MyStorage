package air.kanna.mystorage.config;

import java.util.ArrayList;
import java.util.List;

public class MyStorageConfig {
    public static final int DEFAULT_PAGE_SIZE = 50;
    
    private String dbPath = "DB";
    private String backPath = "BACKUP";
    private String dbFileName = "MyStorage.db";
    
    private String searchFileName = "";
    private String searchFileType = "";
    private String searchDiskPath = "";
    private String isScanWithHash = "false";
    
    private int pageSize = DEFAULT_PAGE_SIZE;
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
}
