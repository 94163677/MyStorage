package air.kanna.mystorage.backup.impl;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import air.kanna.kindlesync.scan.FileScanner;
import air.kanna.kindlesync.scan.PathScanner;
import air.kanna.kindlesync.scan.filter.FileNameIncludeFilter;
import air.kanna.kindlesync.scan.filter.FileTypeFilter;
import air.kanna.kindlesync.scan.filter.NullDirectoryFilter;
import air.kanna.mystorage.backup.BackupService;
import air.kanna.mystorage.util.NumberUtil;

public class LocalBackupServiceImpl implements BackupService {
    private static final Logger logger = Logger.getLogger(LocalBackupServiceImpl.class);
    
    /**
     * 命名规范：原文件名 + '.' + 格式化后的备份日期 + '.' + 补0后的序号 + '.' + 备份后缀
     */
    //备份后缀
    private static final String BACKUP_END = "bak";
    //备份文件名称中的日期格式
    private static final String DATE_FORMAT = "yyyyMMdd";
    //补0序号的长度
    private static final int FIX_NUMBER = 6;
    
  //是否按日期备份
    private boolean isBackupByDate = false;
    //最大备份数(如果按日期备份，则表示每个日期的最大备份数)
    private int maxBackupNum = DEFAULT_BACKUP_SIZE;
    //最大备份日期数
    private int maxBackupDay = DEFAULT_BACKUP_DAY;
    
    @Override
    public boolean backup(File source, Object dest, String... params) {
        if(dest == null || !(dest instanceof File)) {
            logger.error("dest is null");
            return false;
        }
        return backup(source, (File)dest);
    }
    
    private boolean backup(File source, File dest) {
        if(source == null || dest == null) {
            logger.error("source file or dest file is null");
            return false;
        }
        if(!(source.exists() && source.isFile())) {
            logger.error("source file is not a file");
            return false;
        }
        if(!(dest.exists() && dest.isDirectory())) {
            logger.error("dest file is not a directory");
            return false;
        }
        if(isBackupByDate) {
            return backupByDate(source, dest);
        }else {
            return backupBySize(source, dest, true);
        }
    }
    
    private boolean backupByDate(File source, File dest) {
        String dateStr = new SimpleDateFormat(DATE_FORMAT).format(new Date());
        File path = new File(dest, dateStr);
        boolean result = false;
        
        if(!path.exists()) {
            if(!backupFile(source, path, 1L)) {
                return false;
            }
            return true;
        }
        
        result = backupBySize(source, path, false);
        
        FileScanner scanner = new PathScanner();
      //扫描备份目录下所有名字与备份
        ((PathScanner)scanner).setAddBasePath(false);
        ((PathScanner)scanner).setScanDirectoryWhenUnaccept(true);
        ((PathScanner)scanner).getFilters().add(new FileTypeFilter().addType(FileTypeFilter.TYPE_FILE));
        ((PathScanner)scanner).getFilters().add(new FileNameIncludeFilter(source.getName()));
        ((PathScanner)scanner).getFilters().add(new FileNameIncludeFilter(BACKUP_END));
        
        List<File> allDateFile = scanner.scan(dest);
        List<File> delFiles = new ArrayList<>();
        List<String> dateNames = new ArrayList<>();
        
        Comparator<File> comparator = (File a, File b) -> b.getName().compareTo(a.getName());
        Collections.sort(allDateFile, comparator);
        
        for(File file : allDateFile) {
            String name = file.getParentFile().getName();
            if(dateNames.contains(name)) {
                continue;
            }
            if(dateNames.size() >= maxBackupDay) {
                delFiles.add(file);
            }else {
                dateNames.add(name);
            }
        }
        
        deleteFiles(delFiles);
        deleteNullDirectory(dest);
        
        return result;
    }
    
    private boolean backupBySize(File source, File dest, boolean isCreateSubPath) {
        FileScanner scanner = new PathScanner();
        long maxNumber = 0;
        
        //扫描备份目录下所有名字与备份
        ((PathScanner)scanner).setAddBasePath(false);
        ((PathScanner)scanner).setScanDirectoryWhenUnaccept(true);
        ((PathScanner)scanner).getFilters().add(new FileTypeFilter().addType(FileTypeFilter.TYPE_FILE));
        ((PathScanner)scanner).getFilters().add(new FileNameIncludeFilter(source.getName()));
        ((PathScanner)scanner).getFilters().add(new FileNameIncludeFilter(BACKUP_END));
        
        List<File> backFiles = scanner.scan(dest);
        List<File> delFiles = new ArrayList<>();
        
        if(backFiles == null || backFiles.size() <= 0) {
            maxNumber = 1;
        }else {
            Comparator<File> comparator = (File a, File b) -> a.getName().compareTo(b.getName());
            File maxNumFile = null;
            int delNum = backFiles.size() - maxBackupNum + 1;
            
          //按文件名称降序排序
            Collections.sort(backFiles, comparator);
            //找到要删除的文件
            for(int i=0; i<delNum; i++) {
                delFiles.add(backFiles.get(i));
            }
            
            //获取本次备份文件的序号
            maxNumFile = backFiles.get(backFiles.size() - 1);
            try {
                int sourceNameLength = source.getName().length() + DATE_FORMAT.length() + 2;
                
                Long result = NumberUtil.fromFixedLength(
                        maxNumFile.getName().substring(
                                sourceNameLength, (sourceNameLength + FIX_NUMBER)));
                if(result != null) {
                    maxNumber = result.longValue();
                    maxNumber++;
                }
            }catch(Exception e) {
                logger.error("Cannot get backup number at file: " + maxNumFile.getName());
            }
        }
        
        if(maxNumber > 999999L) {
            maxNumber = 1;
        }
        
        boolean success = false;
        if(isCreateSubPath) {
            String dateStr = new SimpleDateFormat(DATE_FORMAT).format(new Date());
            File path = new File(dest, dateStr);
            success = backupFile(source, path, maxNumber);
        }else {
            success = backupFile(source, dest, maxNumber);
        }
        if(!success) {
            return false;
        }
        
        deleteFiles(delFiles);
        if(isCreateSubPath) {
            deleteNullDirectory(dest);
        }
        
        return true;
    }
    
    private boolean backupFile(File source, File path, long number) {
        if(!path.exists()) {
            if(!path.mkdirs()) {
                logger.error("Cannot create path: " + path.getAbsolutePath());
                return false;
            }
        }
        int count = 0;
        for(; count<(maxBackupNum + 1) && number<=999999L; count++, number++) {
            StringBuilder backName = new StringBuilder();
            backName.append(source.getName()).append('.')
                .append(path.getName()).append('.')
                .append(NumberUtil.toFixedLength(number, FIX_NUMBER)).append('.')
                .append(BACKUP_END);
            File backFile = new File(path, backName.toString());
            if(backFile.exists()) {
                continue;
            }
            try {
                Files.copy(source.toPath(), backFile.toPath(), StandardCopyOption.COPY_ATTRIBUTES);
            } catch (IOException e) {
                logger.error("backup error: " + e.getMessage(), e);
                return false;
            }
            break;
        }
        if(count<(maxBackupNum + 1) && number<=999999L) {
            return true;
        }else {
            logger.error("Cannot backup with number: " + number);
            return false;
        }
    }
    
    private void deleteFiles(List<File> files) {
        if(files == null || files.size() <= 0) {
            return;
        }

        for(File file : files) {
            if(!(file.exists() && file.isFile())) {
                continue;
            }
            if(!file.delete()) {
                logger.error("delete file error: " + file.getAbsolutePath());
            }
        }
    }
    
    private void deleteNullDirectory(File path) {
        if(path == null || !path.isDirectory()) {
            return;
        }
        File[] list = path.listFiles();
        NullDirectoryFilter filter = new NullDirectoryFilter();
        
        for(int i=0; i<list.length; i++) {
            if(list[i] == null || !list[i].isDirectory()) {
                continue;
            }
            if(!filter.accept(list[i])) {
                if(!list[i].delete()) {
                    logger.error("delete null Directory error: " + list[i].getAbsolutePath());
                }
            }
        }
    }
    
    
    

    public boolean isBackupByDate() {
        return isBackupByDate;
    }

    public void setBackupByDate(boolean isBackupByDate) {
        this.isBackupByDate = isBackupByDate;
    }

    public int getMaxBackupNum() {
        return maxBackupNum;
    }

    public void setMaxBackupNum(int maxBackupNum) {
        this.maxBackupNum = maxBackupNum;
    }

    public int getMaxBackupDay() {
        return maxBackupDay;
    }

    public void setMaxBackupDay(int maxBackupDay) {
        this.maxBackupDay = maxBackupDay;
    }
}
