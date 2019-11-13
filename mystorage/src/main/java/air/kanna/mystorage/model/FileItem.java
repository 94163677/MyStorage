package air.kanna.mystorage.model;

import air.kanna.mystorage.model.dto.FileItemDTO;
import air.kanna.mystorage.util.DateTimeUtil;
import air.kanna.mystorage.util.StringUtil;

public class FileItem extends FileItemDTO{
    
    public FileItem(FileItemDTO dto) {
        fromDto(dto);
    }
    
    private void fromDto(FileItemDTO dto) {
        if(dto == null) {
            throw new NullPointerException("FileItemDto is null");
        }
        
        setId(dto.getId());
        
        if(dto.getDiskId() <= 0) {
            throw new NullPointerException("FileItemDto's DiskId is error: " + dto.getDiskId());
        }
        setDiskId(dto.getDiskId());
        
        if(StringUtil.isNull(dto.getFileName())) {
            throw new NullPointerException("FileItemDto's FileName is null");
        }
        setFileName(dto.getFileName());
        
        setFileType(dto.getFileType());
        
        setFileSize(dto.getFileSize());
        
        if(StringUtil.isNull(dto.getFilePath())) {
            throw new NullPointerException("FileItemDto's FilePath is null");
        }
        setFilePath(dto.getFilePath());
        
        setFileHash01(dto.getFileHash01());
        setFileHash02(dto.getFileHash02());
        setFileHash03(dto.getFileHash03());
        
        setCreateDate(dto.getCreateDate());
        setLastModDate(dto.getLastModDate());
        setRemark(dto.getRemark());
    }
    
    /*
     * 文件所属磁盘/目录
     */
    private DiskDescription diskDescription = null;
    
    /*
     * 文件类型
     */
    private FileType fileTypeObj = FileType.TYPE_ROOT;
    
    /*
     * 文件创建日期
     * 格式：YYYY-MM-DD HH:mm:SS
     */
    private String createDateStr = "";
    
    /*
     * 文件最后修改日期
     * 格式：YYYY-MM-DD HH:mm:SS
     */
    private String lastModDateStr = "";

    
    public FileType getFileTypeObj() {
        return fileTypeObj;
    }

    public void setFileTypeObj(FileType fileTypeObj) {
        if(fileTypeObj == null) {
            throw new NullPointerException("FileType is null");
        }
        this.fileTypeObj = fileTypeObj;
        this.fileType = fileTypeObj.getType();
    }
    
    @Override
    public void setFileType(char fileType) {
        FileType type = FileType.getFromChar(fileType);
        if(type == null) {
            throw new IllegalArgumentException("Cannot find fileType: " + fileType);
        }
        fileTypeObj = type;
        this.fileType = type.getType();
    }

    public String getCreateDateStr() {
        return createDateStr;
    }

    public void setCreateDateStr(String createDateStr) {
        this.createDate = DateTimeUtil.getDateTimeFromString(createDateStr);
        this.createDateStr = createDateStr;
    }
    
    @Override
    public void setCreateDate(long createDate) {
        this.createDateStr = DateTimeUtil.getStringFromDateTime(createDate);
        this.createDate = createDate;
    }

    public String getLastModDateStr() {
        return lastModDateStr;
    }

    public void setLastModDateStr(String lastModDateStr) {
        this.lastModDate = DateTimeUtil.getDateTimeFromString(lastModDateStr);
        this.lastModDateStr = lastModDateStr;
    }
    
    @Override
    public void setLastModDate(long lastModDate) {
        this.lastModDateStr = DateTimeUtil.getStringFromDateTime(lastModDate);
        this.lastModDate = lastModDate;
    }

    public DiskDescription getDiskDescription() {
        return diskDescription;
    }

    public void setDiskDescription(DiskDescription diskDescription) {
        if(getDiskId() != diskDescription.getId()) {
            throw new IllegalArgumentException("DiskDescription cannot match fileItem's diskId");
        }
        this.diskDescription = diskDescription;
    }
}
