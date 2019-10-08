package air.kanna.mystorage.model;

/**
 * 文件类型
 */
public class FileType {

    public static final FileType TYPE_ROOT = new FileType('0', "根目录");
    public static final FileType TYPE_FILE = new FileType('F', "文件");
    public static final FileType TYPE_DICECTORY = new FileType('D', "目录");
    public static final FileType TYPE_ZIP = new FileType('Z', "压缩包");
    
    private char type;
    private String description;
    
    /**
     * 根据文件类型的特征字符，返回文件类型的类
     * @param type
     * @return
     */
    public static FileType getFromChar(char type) {
        if(TYPE_ROOT.type == type) {
            return TYPE_ROOT;
        }
        if(TYPE_FILE.type == type) {
            return TYPE_FILE;
        }
        if(TYPE_DICECTORY.type == type) {
            return TYPE_DICECTORY;
        }
        if(TYPE_ZIP.type == type) {
            return TYPE_ZIP;
        }
        return null;
    }
    
    
    private FileType(char type, String description) {
        this.type = type;
        this.description = description;
    }

    public char getType() {
        return type;
    }
    public String getDescription() {
        return description;
    }
}
