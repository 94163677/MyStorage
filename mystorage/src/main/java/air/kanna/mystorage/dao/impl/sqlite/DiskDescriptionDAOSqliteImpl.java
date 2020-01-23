package air.kanna.mystorage.dao.impl.sqlite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.apache.log4j.Logger;

import air.kanna.mystorage.dao.DiskDescriptionDAO;
import air.kanna.mystorage.dao.OrderBy;
import air.kanna.mystorage.dao.Pager;
import air.kanna.mystorage.model.dto.DiskDescriptionDTO;
import air.kanna.mystorage.util.DateTimeUtil;
import air.kanna.mystorage.util.StringUtil;

public class DiskDescriptionDAOSqliteImpl 
    extends BaseSqliteDAO<DiskDescriptionDTO> 
    implements DiskDescriptionDAO {
    private static final Logger logger = Logger.getLogger(DiskDescriptionDAOSqliteImpl.class);
    
    public DiskDescriptionDAOSqliteImpl(Connection conn) throws SQLException {
        super(conn);
    }

    @Override
    public int update(DiskDescriptionDTO object) {
        if(object != null && object.getId() <= 0) {
            logger.warn("DiskDescriptionDTO's id is < 0");
            return -1;
        }
        return super.update(object);
    }

    @Override
    public int delete(DiskDescriptionDTO object) {
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
    public List<DiskDescriptionDTO> listByCondition(
            String basePath, String desc, OrderBy order, Pager pager) {
        String sql = getConditionSQL(basePath, desc, order, pager);
        List<DiskDescriptionDTO> list = new ArrayList<>();
        logger.info(sql);
        
        try {
            ResultSet result = stat.executeQuery(sql);

            if(result == null) {
                return null;
            }
        
            for(;result.next();) {
                DiskDescriptionDTO object = resultSet2Object(result);
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
    protected String getTableName() {
        return "disk_description";
    }

    @Override
    protected String getKeyCloumName() {
        return "id";
    }

    @Override
    protected DiskDescriptionDTO resultSet2Object(ResultSet result) throws SQLException {
        DiskDescriptionDTO disk = new DiskDescriptionDTO();
            
        disk.setId(result.getLong("id"));
        disk.setVersion(result.getString("version"));
        disk.setBasePath(result.getString("base_path"));
        disk.setDescription(result.getString("description"));
        disk.setLastUpdate(result.getString("last_update"));
            
        return disk;
    }
    
    @Override
    protected String getInsertSQL(DiskDescriptionDTO object) {
        StringBuilder sb = new StringBuilder();
        sb.append(" INSERT INTO ").append(getTableName())
            .append("(version, base_path, description, last_update) ")
            .append(" VALUES( \'")
            .append(object.getVersion()).append("\', \'")
            .append(getFixedString(object.getBasePath())).append("\', \'")
            .append(object.getDescription()).append("\', \'")
            .append(object.getLastUpdate()).append("\')");
        return sb.toString();
    }
    
    @Override
    protected String getUpdateSQL(DiskDescriptionDTO object) {
        StringBuilder sb = new StringBuilder();
        sb.append(" UPDATE ").append(getTableName())
            .append(" SET last_update = \'").append(DateTimeUtil.getDateTimeString(new Date())).append('\'');
        
        if(StringUtil.isNotNull(object.getVersion())) {
            sb.append(", version = \'").append(object.getVersion()).append('\'');
        }
        if(StringUtil.isNotNull(object.getBasePath())) {
            sb.append(", base_path = \'").append(getFixedString(object.getBasePath())).append('\'');
        }
        if(StringUtil.isNotNull(object.getDescription())) {
            sb.append(", description = \'").append(object.getDescription()).append('\'');
        }
        
        sb.append(" WHERE id = ").append(object.getId());
        return sb.toString();
    }
    
    private String getConditionSQL(
            String basePath, String desc, OrderBy order, Pager pager) {
        StringBuilder sb = new StringBuilder();
        sb.append(" SELECT * FROM ").append(getTableName()).append(" WHERE 1 = 1 ");
        
        if(StringUtil.isNotNull(basePath)) {
            sb.append(" AND base_path LIKE \'%").append(basePath).append("%\'");
        }
        if(StringUtil.isNotNull(desc)) {
            sb.append(" AND description LIKE \'%").append(desc).append("%\'");
        }
        sb.append(getOrderSQL(order));
        sb.append(getPagerSQL(pager));
        return sb.toString();
    }
}
