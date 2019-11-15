package air.kanna.mystorage.config.impl;

import java.io.File;
import java.util.Properties;

import org.apache.log4j.Logger;

import air.kanna.kindlesync.config.impl.BaseFileConfigService;
import air.kanna.kindlesync.util.Nullable;
import air.kanna.mystorage.config.MyStorageConfig;
import air.kanna.mystorage.config.MyStorageConfigService;

public class MyStorageConfigServicePropertiesImpl 
        extends BaseFileConfigService<MyStorageConfig> 
        implements MyStorageConfigService{
    private static final Logger logger = Logger.getLogger(MyStorageConfigServicePropertiesImpl.class);
    
    public MyStorageConfigServicePropertiesImpl(File propFile) {
        super(propFile);
    }

    @Override
    protected MyStorageConfig prop2Config(Properties prop) {
        if(prop == null || prop.size() <= 0) {
            return null;
        }
        MyStorageConfig config = new MyStorageConfig();
        
        String temp = prop.getProperty("dbPath");
        if(!Nullable.isNull(temp)) {
            config.setDbPath(temp);
        }
        
        temp = prop.getProperty("backPath");
        if(!Nullable.isNull(temp)) {
            config.setBackPath(temp);
        }
        
        temp = prop.getProperty("dbFileName");
        if(!Nullable.isNull(temp)) {
            config.setDbFileName(temp);
        }
        
        
        temp = prop.getProperty("searchFileName");
        if(!Nullable.isNull(temp)) {
            config.setSearchFileName(temp);
        }
        
        temp = prop.getProperty("searchFileType");
        if(!Nullable.isNull(temp)) {
            config.setSearchFileType(temp);
        }
        
        temp = prop.getProperty("searchDiskPath");
        if(!Nullable.isNull(temp)) {
            config.setSearchDiskPath(temp);
        }
        
        temp = prop.getProperty("isScanWithHash");
        if("true".equalsIgnoreCase(temp)) {
            config.setIsScanWithHash("true");
        }else {
            config.setIsScanWithHash("false");
        }
        
        temp = prop.getProperty("isAutoBackup");
        if("true".equalsIgnoreCase(temp)) {
            config.setIsAutoBackup("true");
        }else {
            config.setIsAutoBackup("false");
        }
        
        temp = prop.getProperty("isBackupByDate");
        if("true".equalsIgnoreCase(temp)) {
            config.setIsBackupByDate("true");
        }else {
            config.setIsBackupByDate("false");
        }
        
        temp = prop.getProperty("maxBackupNum");
        if(!Nullable.isNull(temp)) {
            try {
                int backNum = Integer.parseInt(temp);
                if(backNum > 0) {
                    config.setMaxBackupNum(backNum);
                }
            }catch(Exception e) {
                logger.warn("parse page size error", e);
            }
        }
        
        temp = prop.getProperty("maxBackupDay");
        if(!Nullable.isNull(temp)) {
            try {
                int backDay = Integer.parseInt(temp);
                if(backDay > 0) {
                    config.setMaxBackupDay(backDay);
                }
            }catch(Exception e) {
                logger.warn("parse page size error", e);
            }
        }

        temp = prop.getProperty("pageSize");
        if(!Nullable.isNull(temp)) {
            try {
                int pagesize = Integer.parseInt(temp);
                if(pagesize > 0) {
                    config.setPageSize(pagesize);
                }
            }catch(Exception e) {
                logger.warn("parse page size error", e);
            }
        }
        
        temp = prop.getProperty("tableColumnWidth");
        if(!Nullable.isNull(temp)) {
            String[] widths = temp.split(";");
            if(widths == null) {
                widths = new String[] {temp};
            }
            for(int i=0; i<widths.length; i++) {
                if(Nullable.isNull(widths[i])) {
                    continue;
                }
                try {
                    config.getTableColumnWidth().add(Integer.parseInt(widths[i]));
                }catch(Exception e) {
                    logger.warn("tableColumnWidth parse error at " + i, e);
                    config.getTableColumnWidth().add(0);
                }
            }
        }

        return config;
    }

    @Override
    protected Properties config2Prop(MyStorageConfig config) {
        if(config == null) {
            return null;
        }
        Properties prop = new Properties();
        
        prop.put("dbPath", config.getDbPath());
        prop.put("backPath", config.getBackPath());
        prop.put("dbFileName", "" + config.getDbFileName());
        
        prop.put("searchFileName", config.getSearchFileName());
        prop.put("searchFileType", config.getSearchFileType());
        prop.put("searchDiskPath", config.getSearchDiskPath());
        prop.put("isScanWithHash", config.getIsScanWithHash());
        
        prop.put("pageSize", ("" + config.getPageSize()));
        prop.put("isAutoBackup", config.getIsAutoBackup());
        prop.put("isBackupByDate", config.getIsBackupByDate());
        prop.put("maxBackupNum", ("" + config.getMaxBackupNum()));
        prop.put("maxBackupDay", ("" + config.getMaxBackupDay()));
        
        
        StringBuilder sb = new StringBuilder();
        for(Integer width : config.getTableColumnWidth()) {
            if(width != null) {
                sb.append(width.intValue()).append(';');
            }
        }
        prop.put("tableColumnWidth", sb.toString());
        
        return prop;
    }

}
