package air.kanna.mystorage.sync.model;

public class FileInformation {
    private int fileId;
    private long fileSize;
    private int dataSize;
    
    private String fileName;
    private String fileHash;
    
    
    public int getFileId() {
        return fileId;
    }
    public void setFileId(int fileId) {
        this.fileId = fileId;
    }
    public long getFileSize() {
        return fileSize;
    }
    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }
    public int getDataSize() {
        return dataSize;
    }
    public String getFileName() {
        return fileName;
    }
    public String getFileHash() {
        return fileHash;
    }
    public void setDataSize(int dataSize) {
        this.dataSize = dataSize;
    }
    public void setFileName(String fileName) {
        this.fileName = fileName;
    }
    public void setFileHash(String fileHash) {
        this.fileHash = fileHash;
    }
}
