package air.kanna.mystorage.sync.model;

public class FileData {
    private int fileId;
    private int dataNum;
    private String data;
    

    public int getFileId() {
        return fileId;
    }
    public void setFileId(int fileId) {
        this.fileId = fileId;
    }
    public int getDataNum() {
        return dataNum;
    }
    public String getData() {
        return data;
    }
    public void setDataNum(int dataNum) {
        this.dataNum = dataNum;
    }
    public void setData(String data) {
        this.data = data;
    }
}
