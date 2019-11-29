package air.kanna.mystorage.dao.condition;

import java.util.ArrayList;
import java.util.List;

public class FileItemConditionExt extends FileItemCondition{

    private List<String> fileNameIncludeAll = new ArrayList<>();
    private List<String> fileNameIncludeOne = new ArrayList<>();
    private List<String> fileNameExclude = new ArrayList<>();
    
    public List<String> getFileNameIncludeAll() {
        return fileNameIncludeAll;
    }
    public List<String> getFileNameIncludeOne() {
        return fileNameIncludeOne;
    }
    public List<String> getFileNameExclude() {
        return fileNameExclude;
    }
    public void setFileNameIncludeAll(List<String> fileNameIncludeAll) {
        this.fileNameIncludeAll.clear();
        if(fileNameIncludeAll != null && fileNameIncludeAll.size() > 0) {
            this.fileNameIncludeAll.addAll(fileNameIncludeAll);
        }
    }
    public void setFileNameIncludeOne(List<String> fileNameIncludeOne) {
        this.fileNameIncludeOne.clear();
        if(fileNameIncludeOne != null && fileNameIncludeOne.size() > 0) {
            this.fileNameIncludeOne.addAll(fileNameIncludeOne);
        }
    }
    public void setFileNameExclude(List<String> fileNameExclude) {
        this.fileNameExclude.clear();
        if(fileNameExclude != null && fileNameExclude.size() > 0) {
            this.fileNameExclude.addAll(fileNameExclude);
        }
    }
}
