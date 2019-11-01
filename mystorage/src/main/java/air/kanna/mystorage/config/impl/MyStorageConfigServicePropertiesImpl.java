package air.kanna.mystorage.config.impl;

import java.io.File;
import java.util.Properties;

import air.kanna.kindlesync.config.impl.BaseFileConfigService;
import air.kanna.kindlesync.util.Nullable;
import air.kanna.mystorage.config.MyStorageConfig;
import air.kanna.mystorage.config.MyStorageConfigService;

public class MyStorageConfigServicePropertiesImpl 
        extends BaseFileConfigService<MyStorageConfig> 
        implements MyStorageConfigService{

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
        
        return prop;
    }

}
