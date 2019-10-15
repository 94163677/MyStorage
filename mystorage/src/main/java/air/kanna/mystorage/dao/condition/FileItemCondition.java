package air.kanna.mystorage.dao.condition;

public class FileItemCondition {
    private Long diskId;
    private String fileName;
    private char fileType;
    
    private Long fileSizeMin;
    private Long fileSizeMax;
    
    private String filePath;
    
    private Long createDateMin;
    private Long createDateMax;
    
    private Long lastModMin;
    private Long lastModMax;
    
    
    
    
    public Long getDiskId() {
        return diskId;
    }
    public void setDiskId(Long diskId) {
        this.diskId = diskId;
    }
    public String getFileName() {
        return fileName;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public char getFileType() {
        return fileType;
    }
    public void setFileType(char fileType) {
        this.fileType = fileType;
    }
    public Long getFileSizeMin() {
        return fileSizeMin;
    }
    public void setFileSizeMin(Long fileSizeMin) {
        this.fileSizeMin = fileSizeMin;
    }
    public Long getFileSizeMax() {
        return fileSizeMax;
    }
    public void setFileSizeMax(Long fileSizeMax) {
        this.fileSizeMax = fileSizeMax;
    }
    public String getFilePath() {
        return filePath;
    }
    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }
    public Long getCreateDateMin() {
        return createDateMin;
    }
    public void setCreateDateMin(Long createDateMin) {
        this.createDateMin = createDateMin;
    }
    public Long getCreateDateMax() {
        return createDateMax;
    }
    public void setCreateDateMax(Long createDateMax) {
        this.createDateMax = createDateMax;
    }
    public Long getLastModMin() {
        return lastModMin;
    }
    public void setLastModMin(Long lastModMin) {
        this.lastModMin = lastModMin;
    }
    public Long getLastModMax() {
        return lastModMax;
    }
    public void setLastModMax(Long lastModMax) {
        this.lastModMax = lastModMax;
    }
}
