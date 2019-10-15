package air.kanna.mystorage.model.dto;

import air.kanna.mystorage.model.FileType;

public class FileItemDTO {

    /*
     * 唯一键
     */
    private long id = 0;
    
    /*
     * 磁盘ID
     */
    private long diskId = 0L;

    /*
     * 文件名称
     * 512字节
     */
    private String fileName = "";
    
    /*
     * 文件类型，详见FileType
     * 1字节
     */
    protected char fileType = FileType.TYPE_ROOT.getType();
    
    /*
     * 文件长度
     * 8字节
     */
    private long fileSize = 0l;
    
    /*
     * 文件路径
     * 2k字节（2048）
     */
    private String filePath = "";
    
    /*
     * 文件Hash码01
     * 只有fileType为TYPE_FILE或者TYPE_ZIP才有
     * 本字段暂定是MD5编码
     * 128字节
     */
    private String fileHash01 = "";
    
    /*
     * 文件Hash码02
     * 只有fileType为TYPE_FILE或者TYPE_ZIP才有
     * 本字段按项目实际情况选择散列算法
     * 128字节
     */
    private String fileHash02 = "";
    
    /*
     * 文件Hash码03
     * 只有fileType为TYPE_FILE或者TYPE_ZIP才有
     * 本字段按项目实际情况选择散列算法
     * 128字节
     */
    private String fileHash03 = "";
    
    /*
     * 文件创建日期
     * 19字节
     * 格式：YYYY-MM-DD HH:mm:SS
     */
    protected long createDate = -1L;
    
    /*
     * 文件最后修改日期
     * 19字节
     * 格式：YYYY-MM-DD HH:mm:SS
     */
    protected long lastModDate = -1L;
    
    /*
     * 文件备注
     * 300字节
     */
    private String remark = "";
    
    
    

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public long getDiskId() {
        return diskId;
    }

    public void setDiskId(long diskId) {
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

    public long getFileSize() {
        return fileSize;
    }

    public void setFileSize(long fileSize) {
        this.fileSize = fileSize;
    }

    public String getFilePath() {
        return filePath;
    }

    public void setFilePath(String filePath) {
        this.filePath = filePath;
    }

    public String getFileHash01() {
        return fileHash01;
    }

    public void setFileHash01(String fileHash01) {
        this.fileHash01 = fileHash01;
    }

    public String getFileHash02() {
        return fileHash02;
    }

    public void setFileHash02(String fileHash02) {
        this.fileHash02 = fileHash02;
    }

    public String getFileHash03() {
        return fileHash03;
    }

    public void setFileHash03(String fileHash03) {
        this.fileHash03 = fileHash03;
    }

    public long getCreateDate() {
        return createDate;
    }

    public void setCreateDate(long createDate) {
        this.createDate = createDate;
    }

    public long getLastModDate() {
        return lastModDate;
    }

    public void setLastModDate(long lastModDate) {
        this.lastModDate = lastModDate;
    }

    public String getRemark() {
        return remark;
    }

    public void setRemark(String remark) {
        this.remark = remark;
    }
}
