package air.kanna.mystorage.service.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import air.kanna.kindlesync.scan.FileScanner;
import air.kanna.mystorage.model.DiskDescription;
import air.kanna.mystorage.model.FileItem;
import air.kanna.mystorage.model.FileType;
import air.kanna.mystorage.model.dto.FileItemDTO;
import air.kanna.mystorage.service.SourceFileItemGetter;
import air.kanna.mystorage.util.DateTimeUtil;
import air.kanna.mystorage.util.StringUtil;

public class LocalSourceFileItemGetter implements SourceFileItemGetter {
    private static final Logger logger = Logger.getLogger(LocalSourceFileItemGetter.class);
    
    private FileScanner scanner;
    
    @Override
    public List<FileItem> createNewDiskFileItem(DiskDescription disk) {
        if(disk == null || disk.getId() <= 0 || StringUtil.isNull(disk.getBasePath())) {
            throw new NullPointerException("disk or basePath is null");
        }
        File basePath = new File(disk.getBasePath());
        List<File> fileList = scanner.scan(basePath);
        if(fileList == null || fileList.size() <= 0) {
            return null;
        }
        return fileToFileItem(disk, fileList);
    }
    
    private List<FileItem> fileToFileItem(DiskDescription disk, List<File> fileList){
        FileItem item = null;
        List<FileItem> itemList = new ArrayList<>(fileList.size());
        
        for(File file : fileList) {
            if(file == null) {
                continue;
            }
            item = fileToFileItem(disk, file);
            if(item != null) {
                itemList.add(item);
            }
        }
        return itemList;
    }
    
    private FileItem fileToFileItem(DiskDescription disk, File file) {
        String path = file.getAbsolutePath();
        if(path.equalsIgnoreCase(disk.getBasePath())) {
            return null;
        }
        path = path.substring(disk.getBasePath().length());
        
        FileItemDTO item = new FileItemDTO();
        item.setDiskId(disk.getId());
        item.setFileName(file.getName());
        if(file.isDirectory()) {
            item.setFileType(FileType.TYPE_DICECTORY.getType());
        }
        if(file.isFile()) {
            item.setFileType(FileType.TYPE_FILE.getType());
        }
        item.setFileSize(file.length());
        item.setFilePath(path);
        
        readFileTimes(file, item);
        FileItem fileItem = new FileItem(item);
        fileItem.setDiskDescription(disk);
        
        return fileItem;
    }
    
    private void readFileTimes(File file, FileItemDTO item) {
        BasicFileAttributes bAttributes = null;
        try {
            Path path = Paths.get(file.getAbsolutePath());
            bAttributes = Files.readAttributes(path, 
                BasicFileAttributes.class);
            
            if(bAttributes == null) {
                return;
            }
            item.setCreateDate(
                    DateTimeUtil.getDateTimeFromTimeMillis(
                            bAttributes.creationTime().toMillis()));
            item.setLastModDate(
                    DateTimeUtil.getDateTimeFromTimeMillis(
                            bAttributes.lastModifiedTime().toMillis()));
        } catch (Exception e) {
            logger.warn("Cannot read File's times: " + file.getAbsolutePath(), e);
            try{
	            item.setLastModDate(
	                    DateTimeUtil.getDateTimeFromTimeMillis(
	                            file.lastModified()));
            }catch(Exception e2){
            	logger.warn("Cannot read File's times: " + file.getAbsolutePath(), e2);
            }
        }
    }

    public FileScanner getScanner() {
        return scanner;
    }

    public void setScanner(FileScanner scanner) {
        this.scanner = scanner;
    }
}
