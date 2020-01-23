package air.kanna.mystorage.dao.impl.sqlite;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import air.kanna.mystorage.dao.condition.FileItemCondition;
import air.kanna.mystorage.dao.condition.FileItemConditionExt;
import air.kanna.mystorage.util.StringUtil;

public class FileItemDAOSqliteExtImpl extends FileItemDAOSqliteImpl{
    private static final Logger logger = Logger.getLogger(FileItemDAOSqliteExtImpl.class);
    
    public FileItemDAOSqliteExtImpl(Connection conn) throws SQLException {
        super(conn);
    }

    @Override
    protected String getConditionSQL(FileItemCondition condition) {
        if(condition instanceof FileItemConditionExt) {
            return getConditionSQLExt((FileItemConditionExt)condition);
        }
        return super.getConditionSQL(condition);
    }
    
    private String getConditionSQLExt(FileItemConditionExt ext) {
        StringBuilder sb = new StringBuilder();
        
        if(ext.getDiskId() != null && ext.getDiskId() > 0) {
            sb.append(" AND disk_id = ").append(ext.getDiskId());
        }
        
        List<String> nameCnd = checkAndGetUnSpace(ext.getFileNameIncludeAll());
        if(nameCnd.size() > 0) {
            for(String incAll : nameCnd) {
                sb.append(" AND file_name LIKE \'%").append(incAll).append("%\'");
            }
        }
        
        nameCnd = checkAndGetUnSpace(ext.getFileNameIncludeOne());
        if(nameCnd.size() > 0) {
            sb.append(" AND ( ");
            for(int i=0; i<nameCnd.size(); i++) {
                String incAll = nameCnd.get(i);
                if(i == 0) {
                    sb.append(" file_name LIKE \'%").append(incAll).append("%\'");
                }else {
                    sb.append(" OR file_name LIKE \'%").append(incAll).append("%\'");
                }
            }
            sb.append(" ) ");
        }
        
        nameCnd = checkAndGetUnSpace(ext.getFileNameExclude());
        if(nameCnd.size() > 0) {
            for(String incAll : nameCnd) {
                sb.append(" AND file_name NOT LIKE \'%").append(incAll).append("%\'");
            }
        }
        
        sb.append(getNormalConditionSQL(ext));
        return sb.toString();
    }
    
    private List<String> checkAndGetUnSpace(List<String> orgList){
        if(orgList == null || orgList.size() <= 0) {
            return new ArrayList<>(1);
        }
        List<String> checked = new ArrayList<>(orgList.size());
        for(String checkStr : orgList) {
            if(StringUtil.isSpace(checkStr)) {
                continue;
            }
            checked.add(checkStr);
        }
        return checked;
    }
    
}
