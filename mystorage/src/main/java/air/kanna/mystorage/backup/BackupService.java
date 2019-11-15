package air.kanna.mystorage.backup;

import java.io.File;

public interface BackupService {
  //默认最大的备份数据文件份数
    static final int DEFAULT_BACKUP_SIZE = 5;
    //默认最大的备份日期数
    static final int DEFAULT_BACKUP_DAY = 5;
    
    boolean backup(File source, Object dest, String... params);
}
