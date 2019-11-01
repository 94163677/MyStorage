package air.kanna.mystorage.service.impl;

import java.util.List;

import air.kanna.kindlesync.scan.PathScanner;
import air.kanna.mystorage.model.DiskDescription;
import air.kanna.mystorage.model.FileItem;
import air.kanna.mystorage.service.SourceFileItemGetter;

public class LocalSourceFileItemGetterTest {

    public static void main(String[] args) {
        PathScanner scanner = new PathScanner();
        SourceFileItemGetter getter = new LocalSourceFileItemGetter();
        
        ((LocalSourceFileItemGetter)getter).setScanner(scanner);
        
        DiskDescription disk = new DiskDescription();
        disk.setId(222);
        disk.setBasePath("D:\\迅雷下载");
        
        List<FileItem> list = getter.createNewDiskFileItem(disk);
        int count = 1;
        for(FileItem item : list) {
            System.out.println(count++ + "\t" + item.getFilePath());
        }
    }
}
