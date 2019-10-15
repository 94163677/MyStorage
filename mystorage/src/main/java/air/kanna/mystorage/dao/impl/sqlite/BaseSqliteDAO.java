package air.kanna.mystorage.dao.impl.sqlite;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import org.apache.log4j.Logger;

import air.kanna.mystorage.dao.BaseModelDAO;
import air.kanna.mystorage.dao.Pager;

public abstract class BaseSqliteDAO<T> implements BaseModelDAO<T>{
    private static final Logger logger = Logger.getLogger(BaseSqliteDAO.class);
    
    protected Connection conn;
    protected Statement stat;
    
    public BaseSqliteDAO(Connection conn) throws SQLException{
        if(conn == null) {
            throw new NullPointerException("Connection is null");
        }
        if(conn.isClosed()) {
            throw new SQLException("Connection is closed");
        }
        if(conn.isReadOnly()) {
            throw new SQLException("Connection is ReadOnly");
        }
        this.conn = conn;
        this.stat = conn.createStatement();
    }
    
    protected abstract String getTableName();
    protected abstract String getKeyCloumName();
    
    @Override
    public int deleteById(Object id) {
        if(id == null) {
            return -1;
        }
        
        StringBuilder sb = new StringBuilder();

        sb.append("DELETE FROM ").append(getTableName())
            .append(" WHERE ").append(getKeyCloumName())
            .append(" = ");
        
        if(id instanceof Number) {
            sb.append(id.toString());
        }else {
            sb.append('\'').append(id.toString()).append('\'');
        }
        
        try {
            return stat.executeUpdate(sb.toString());
        }catch(SQLException e) {
            logger.error("delete error: " + sb.toString(), e);
            return -1;
        }
    }
    
    protected ResultSet getByIdResultSet(Object id) {
        if(id == null) {
            return null;
        }
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT * FROM ").append(getTableName())
            .append(" WHERE ").append(getKeyCloumName())
            .append(" = ");
        
        if(id instanceof Number) {
            sb.append(id.toString());
        }else {
            sb.append('\'').append(id.toString()).append('\'');
        }
        
        try {
            return stat.executeQuery(sb.toString());
        }catch(SQLException e) {
            logger.error("select error: " + sb.toString(), e);
            return null;
        }
    }
    
    protected ResultSet listAllResultSet(Pager pager) {
        StringBuilder sb = new StringBuilder();

        sb.append("SELECT * FROM ").append(getTableName());
        sb.append(getPagerSQL(pager));
        
        try {
            return stat.executeQuery(sb.toString());
        }catch(SQLException e) {
            logger.error("list all error: " + sb.toString(), e);
            return null;
        }
    }
    
    protected String getPagerSQL(Pager pager) {
        if(pager != null) {
            StringBuilder sb = new StringBuilder();
            int offset = (pager.getPage() - 1) * pager.getSize();
            
            sb.append(" LIMIT ").append(pager.getSize());
            sb.append(" OFFSET ").append(offset);
            
            return sb.toString();
        }
        return "";
    }
}
