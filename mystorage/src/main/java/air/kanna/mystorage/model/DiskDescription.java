package air.kanna.mystorage.model;

import air.kanna.mystorage.MyStorage;

/**
 * 磁盘或者根目录的描述
 */
public class DiskDescription {
    public static final int ID_NUMBER_LENGTH = 9;
    
    private String version = MyStorage.VERSION;
    private long id = 0L;
    private String basePath = "";//根路径
    private String description = "";
    private String lastUpdate = "";//YYYY-MM-DD HH:mm:SS
    
    public DiskDescription(int id) {
        if(id <= 0) {
            throw new java.lang.IllegalArgumentException("Disk Id must be lager then 0");
        }
        this.id = id;
    }
    
    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public long getId() {
        return id;
    }
    public String getBasePath() {
        return basePath;
    }
    public void setBasePath(String basePath) {
        this.basePath = basePath;
    }
    public String getDescription() {
        return description;
    }
    public void setDescription(String description) {
        this.description = description;
    }
    public String getLastUpdate() {
        return lastUpdate;
    }
    public void setLastUpdate(String lastUpdate) {
        this.lastUpdate = lastUpdate;
    }
}
