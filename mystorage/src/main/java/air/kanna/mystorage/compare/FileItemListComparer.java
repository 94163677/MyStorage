package air.kanna.mystorage.compare;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import air.kanna.kindlesync.compare.ListComparer;
import air.kanna.kindlesync.compare.Operation;
import air.kanna.kindlesync.compare.OperationItem;
import air.kanna.mystorage.model.FileItem;
import air.kanna.mystorage.model.FileType;

public class FileItemListComparer implements ListComparer<FileItem> {

    @Override
    public List<OperationItem<FileItem>> getCompareResult(List<FileItem> baseFileList, List<FileItem> destFileList) {
        List<OperationItem<FileItem>> result = new ArrayList<>();
        if(baseFileList == null) {
            baseFileList = new ArrayList<>();
        }
        if(destFileList == null) {
            destFileList = new ArrayList<>();
        }
        
        int index = -1;
        FileItem destItem = null;
        OperationItem<FileItem> oper = null;
        Comparator<FileItem> comparator = (FileItem a, FileItem b) -> a.getFilePath().compareTo(b.getFilePath());
        
        Collections.sort(baseFileList, comparator);
        Collections.sort(destFileList, comparator);
        
        for(FileItem file : baseFileList) {
            destItem = null;
            index = Collections.binarySearch(destFileList, file, comparator);
            if(index < 0) {
                oper = new OperationItem<>();
                oper.setItem(file);
                oper.setOperation(Operation.ADD);
                result.add(oper);
                continue;
            }
            if(file.getFileTypeObj() == FileType.TYPE_DICECTORY
                    || file.getFileTypeObj() == FileType.TYPE_ROOT) {
                continue;
            }
            destItem = destFileList.get(index);
            if(file.getFileSize() != destItem.getFileSize()) {
                oper = new OperationItem<>();
                oper.setItem(file);
                oper.setOrgItem(destItem);
                oper.setOperation(Operation.REP);
                result.add(oper);
            }
        }
        
        for(FileItem file : destFileList) {
            index = Collections.binarySearch(baseFileList, file, comparator);
            if(index < 0) {
                oper = new OperationItem<>();
                oper.setItem(file);
                oper.setOperation(Operation.DEL);
                result.add(oper);
                continue;
            }
        }
        
        return result;
    }
}
