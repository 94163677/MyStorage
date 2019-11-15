package air.kanna.mystorage.model;

public enum FileHash {

    /*
     * FileItem.FileHash01
     */
    MD5("MD5"),
    
    /*
     * FileItem.FileHash02
     */
    SHA256("SHA-256");
    
    private final String value;
    
    private FileHash(String value) {
        this.value = value;
    }
    
    public String getValue() {
        return value;
    }
}
