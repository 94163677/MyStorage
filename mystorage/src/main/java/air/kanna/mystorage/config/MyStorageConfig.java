package air.kanna.mystorage.config;

public class MyStorageConfig {
    private String dbPath = "DB";
    private String backPath = "BACKUP";
    private String dbFileName = "MyStorage.db";
    
    private String searchFileName = "";
    private String searchFileType = "";
    private String searchDiskPath = "";
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
}
