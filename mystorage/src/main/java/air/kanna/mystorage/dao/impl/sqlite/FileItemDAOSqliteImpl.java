package air.kanna.mystorage.dao.impl.sqlite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import air.kanna.mystorage.dao.FileItemDAO;
import air.kanna.mystorage.dao.OrderBy;
import air.kanna.mystorage.dao.Pager;
import air.kanna.mystorage.dao.condition.FileItemCondition;
import air.kanna.mystorage.model.dto.FileItemDTO;
import air.kanna.mystorage.util.DateTimeUtil;
import air.kanna.mystorage.util.StringUtil;

public class FileItemDAOSqliteImpl 
    extends BaseSqliteDAO<FileItemDTO> 
    implements FileItemDAO {
    private static final Logger logger = Logger.getLogger(FileItemDAOSqliteImpl.class);

    public FileItemDAOSqliteImpl(Connection conn) throws SQLException {
        super(conn);
    }
    
    @Override
    public int update(FileItemDTO object) {
        if(object != null && object.getId() <= 0) {
            logger.warn("FileItemDTO id is < 0");
            return -1;
        }
        return super.update(object);
    }

    @Override
    public int delete(FileItemDTO object) {
        if(object == null) {
            logger.warn("DiskDescriptionDTO is null");
            return -1;
        }
        if(object.getId() <= 0) {
            logger.warn("DiskDescriptionDTO's id is < 0");
            return -1;
        }
        return deleteById(object.getId());
    }

    @Override
    public List<FileItemDTO> listByCondition(
            FileItemCondition condition, OrderBy order, Pager pager) {
        StringBuilder sql = new StringBuilder();
        String conditionSql = getConditionSQL(condition);
        String fullSql = null;
        List<FileItemDTO> list = new ArrayList<>();
        
        sql.append(" SELECT * FROM ").append(getTableName()).append(" WHERE 1 = 1 ");
        sql.append(conditionSql);
        sql.append(getOrderSQL(order));
        sql.append(getPagerSQL(pager));
        fullSql = sql.toString();
        logger.info(fullSql);
        
        try {
            ResultSet result = stat.executeQuery(fullSql);

            if(result == null) {
                return null;
            }
        
            for(;result.next();) {
                FileItemDTO object = resultSet2Object(result);
                if(object != null) {
                    list.add(object);
                }
            }
        }catch(SQLException e) {
            logger.error("Parse ResultSet error", e);
            return null;
        }
        return list;
    }
    
    @Override
    public int listByConditionCount(FileItemCondition condition) {
        StringBuilder sql = new StringBuilder();
        String conditionSql = getConditionSQL(condition);
        String fullSql = null;
        
        sql.append(" SELECT count(*) FROM ").append(getTableName()).append(" WHERE 1 = 1 ");
        sql.append(conditionSql);
        fullSql = sql.toString();
        logger.info(fullSql);
        
        try {
            ResultSet result = stat.executeQuery(fullSql);

            if(result == null || !result.next()) {
                return -1;
            }
            
            return result.getInt(1);
        }catch(SQLException e) {
            logger.error("list By Condition Count error", e);
            return -1;
        }
    }
    
    @Override
    public int deleteByCondition(FileItemCondition condition) {
        StringBuilder sql = new StringBuilder();
        String conditionSql = getConditionSQL(condition);
        String fullSql = null;
        
        sql.append(" DELETE FROM ").append(getTableName()).append(" WHERE 1 = 1 ");
        sql.append(conditionSql);
        fullSql = sql.toString();
        logger.info(fullSql);
        
        try {
            return stat.executeUpdate(fullSql);
        }catch(SQLException e) {
            logger.error("DELETE FileItem error", e);
            return -1;
        }
    }
    
    @Override
    protected FileItemDTO resultSet2Object(ResultSet result) throws SQLException {
        FileItemDTO item = new FileItemDTO();
        
        item.setId(result.getLong("id"));
        item.setDiskId(result.getLong("disk_id"));
        item.setFileName(result.getString("file_name"));
        item.setFileType((char)result.getLong("file_type"));
        item.setFileSize(result.getLong("file_size"));
        item.setFilePath(result.getString("file_path"));
        
        item.setFileHash01(result.getString("file_hash_01"));
        item.setFileHash02(result.getString("file_hash_02"));
        item.setFileHash03(result.getString("file_hash_03"));
        
        item.setCreateDate(result.getLong("create_date"));
        item.setLastModDate(result.getLong("update_date"));
        item.setRemark(result.getString("remark"));

        return item;
    }

    @Override
    protected String getInsertSQL(FileItemDTO object) {
        StringBuilder sb = new StringBuilder();
        sb.append(" INSERT INTO ").append(getTableName())
            .append("(disk_id, file_name, file_type, file_size, file_path,file_hash_01, ")
            .append("file_hash_02, file_hash_03, create_date, update_date, remark) ")
            .append(" VALUES( ")
            .append(object.getDiskId()).append(", \'")
            .append(object.getFileName()).append("\', ")
            .append((int)object.getFileType()).append(", ")
            .append(object.getFileSize()).append(", \'")
            .append(object.getFilePath()).append("\', \'")
            .append(object.getFileHash01()).append("\', \'")
            .append(object.getFileHash02()).append("\', \'")
            .append(object.getFileHash03()).append("\', ")
            .append(object.getCreateDate()).append(", ")
            .append(object.getLastModDate()).append(", \'")
            .append(object.getRemark()).append("\')");
        return sb.toString();
    }
    
    @Override
    protected String getUpdateSQL(FileItemDTO object) {
        StringBuilder sb = new StringBuilder();
        sb.append(" UPDATE ").append(getTableName())
            .append(" SET update_date = ").append(DateTimeUtil.getDateTimeFromString(DateTimeUtil.getDateTimeString(new Date())));
        
        if(object.getDiskId() > 0) {
            sb.append(", disk_id = ").append(object.getDiskId());
        }
        if(StringUtil.isNotNull(object.getFileName())) {
            sb.append(", file_name = \'").append(object.getFileName()).append('\'');
        }
        if(object.getFileType() >= '0') {
            sb.append(", file_type = ").append((int)object.getFileType());
        }
        if(object.getFileSize() >= 0) {
            sb.append(", file_size = ").append((int)object.getFileSize());
        }
        if(StringUtil.isNotNull(object.getFilePath())) {
            sb.append(", file_path = \'").append(object.getFilePath()).append('\'');
        }
        
        if(StringUtil.isNotNull(object.getFileHash01())) {
            sb.append(", file_hash_01 = \'").append(object.getFileHash01()).append('\'');
        }
        if(StringUtil.isNotNull(object.getFileHash02())) {
            sb.append(", file_hash_02 = \'").append(object.getFileHash02()).append('\'');
        }
        if(StringUtil.isNotNull(object.getFileHash03())) {
            sb.append(", file_hash_03 = \'").append(object.getFileHash03()).append('\'');
        }
        
        if(object.getCreateDate() > 0) {
            sb.append(", create_date = ").append(object.getCreateDate());
        }
        if(StringUtil.isNotNull(object.getRemark())) {
            sb.append(", remark = \'").append(object.getRemark()).append('\'');
        }
        
        sb.append(" WHERE id = ").append(object.getId());
        return sb.toString();
    }

    @Override
    protected String getTableName() {
        return "file_list";
    }

    @Override
    protected String getKeyCloumName() {
        return "id";
    }
    
    private String getConditionSQL(FileItemCondition condition) {
        StringBuilder sb = new StringBuilder();
        
        if(condition.getDiskId() != null && condition.getDiskId() > 0) {
            sb.append(" AND disk_id = ").append(condition.getDiskId());
        }
        if(StringUtil.isNotNull(condition.getFileName())) {
            sb.append(" AND file_name LIKE \'%").append(condition.getFileName()).append("%\'");
        }
        if(condition.getFileType() >= '0') {
            sb.append(" AND fileType = ").append((int)condition.getFileType());
        }
        if(condition.getFileSizeMin() != null) {
            sb.append(" AND file_size >= ").append(condition.getFileSizeMin().longValue());
        }
        if(condition.getFileSizeMax() != null) {
            sb.append(" AND file_size <= ").append(condition.getFileSizeMax().longValue());
        }
        if(StringUtil.isNotNull(condition.getFilePath())) {
            sb.append(" AND file_path LIKE \'%").append(condition.getFilePath()).append("%\'");
        }
        
        if(condition.getCreateDateMin() != null) {
            sb.append(" AND create_date >= ").append(condition.getCreateDateMin().longValue());
        }
        if(condition.getCreateDateMax() != null) {
            sb.append(" AND create_date <= ").append(condition.getCreateDateMax().longValue());
        }
        
        if(condition.getLastModMin() != null) {
            sb.append(" AND update_date >= ").append(condition.getLastModMin().longValue());
        }
        if(condition.getLastModMax() != null) {
            sb.append(" AND update_date <= ").append(condition.getLastModMax().longValue());
        }
        
        return sb.toString();
    }
}
