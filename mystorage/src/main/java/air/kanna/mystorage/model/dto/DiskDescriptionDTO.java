package air.kanna.mystorage.model.dto;

import air.kanna.mystorage.MyStorage;

public class DiskDescriptionDTO {
    private String version = MyStorage.VERSION;
    private long id = -1L;
    private String basePath = "";//根路径
    private String description = "";
    private String lastUpdate = "";//YYYY-MM-DD HH:mm:SS

    public String getVersion() {
        return version;
    }
    public void setVersion(String version) {
        this.version = version;
    }
    public long getId() {
        return id;
    }
    public void setId(long id) {
        this.id = id;
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
