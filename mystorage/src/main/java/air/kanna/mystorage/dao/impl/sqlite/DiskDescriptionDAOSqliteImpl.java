package air.kanna.mystorage.dao.impl.sqlite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;

import air.kanna.mystorage.dao.DiskDescriptionDAO;
import air.kanna.mystorage.dao.Pager;
import air.kanna.mystorage.model.dto.DiskDescriptionDTO;

public class DiskDescriptionDAOSqliteImpl 
    extends BaseSqliteDAO<DiskDescriptionDTO> 
    implements DiskDescriptionDAO {
    private static final Logger logger = Logger.getLogger(DiskDescriptionDAOSqliteImpl.class);
    
    public DiskDescriptionDAOSqliteImpl(Connection conn) throws SQLException {
        super(conn);
    }

    @Override
    public DiskDescriptionDTO getById(Object id) {
        ResultSet result = getByIdResultSet(id);
        if(result == null) {
            return null;
        }
        try {
            if(!result.next()) {
                logger.warn("Cannot found DiskDescription by id: " + id.toString());
                return null;
            }
            
            DiskDescriptionDTO disk = new DiskDescriptionDTO();
            
            
        }catch(SQLException e) {
            logger.error("Parse ResultSet error", e);
        }
        return null;
    }

    @Override
    public List<DiskDescriptionDTO> listAll(Pager pager) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    public int insert(DiskDescriptionDTO object) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int update(DiskDescriptionDTO object) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int delete(DiskDescriptionDTO object) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public int deleteById(Object id) {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public List<DiskDescriptionDTO> listByCondition(String basePath, String desc, Pager pager) {
        // TODO Auto-generated method stub
        return null;
    }

    @Override
    protected String getTableName() {
        return "disk_description";
    }

    @Override
    protected String getKeyCloumName() {
        return "id";
    }

}
