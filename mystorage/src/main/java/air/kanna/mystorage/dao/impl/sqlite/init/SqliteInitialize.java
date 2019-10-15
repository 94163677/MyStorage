package air.kanna.mystorage.dao.impl.sqlite.init;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import air.kanna.mystorage.MyStorage;
import air.kanna.mystorage.util.StringUtil;

public class SqliteInitialize {    
    private static final String PUT_VERSION =
            "INSERT INTO db_version(version) VALUES (\'" + MyStorage.VERSION + "\')";
    private static final String GET_VERSION =
            "SELECT version FROM db_version LIMIT 0,1";
    
    public Connection initAndGetConnection(String dbFilePath) throws SQLException, ClassNotFoundException{
        if(StringUtil.isSpace(dbFilePath)) {
            throw new NullPointerException("dbFilePath is null");
        }
        return initAndGetConnection(new File(dbFilePath));
    }
    
    public Connection initAndGetConnection(File dbFile) throws SQLException, ClassNotFoundException{
        if(dbFile == null) {
            throw new NullPointerException("dbFile is null");
        }
        boolean isNew = false;
        
        if(!dbFile.exists() || !dbFile.isFile()) {
            isNew = true;
        }
        
        Class.forName("org.sqlite.JDBC");
        Connection conn = DriverManager.getConnection("jdbc:sqlite:" + dbFile.getAbsolutePath());
        
        if(isNew) {
            initTables(conn);
        }else {
            checkAndUpdateTables(conn);
        }
        
        return conn;
    }
    
    private void initTables(Connection conn) throws SQLException{
        String sql = getInitTablesSql();
        Statement stat = conn.createStatement();
        
        stat.execute(sql);
        
        int count = stat.executeUpdate(PUT_VERSION);
        if(count <= 0) {
            throw new SQLException("Cannot init version into db");
        }
        
        if(!conn.getAutoCommit()) {
            conn.commit();
        }
        
        stat.close();
    }
    
    private void checkAndUpdateTables(Connection conn) throws SQLException{
        Statement stat = conn.createStatement();
        String version = getDBVersion(stat);
        
        switch(version) {
            case "1.0.0": break;
            default: throw new SQLException("cannot found version: " + version);
        }
    }
    
    private String getDBVersion(Statement stat) throws SQLException{
        ResultSet result = null;
        String version = null;
        
        result = stat.executeQuery(GET_VERSION);
        if(result.next()) {
            version = result.getString(1);
        }
        result.close();
        
        if(StringUtil.isNotNull(version)) {
            return version;
        }
        throw new SQLException("Cannot found db version");
    }
    
    private String getInitTablesSql() throws SQLException{
        try {
            InputStream ins = this.getClass().getResourceAsStream("/air/kanna/mystorage/db/db_init.sql");
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "UTF-8"), 10240);
            StringBuilder sb = new StringBuilder();
            String line = null;
            
            for(line = reader.readLine(); line != null; line = reader.readLine()) {
                sb.append(line).append('\n');
            }
            return sb.toString();
        }catch(Exception e) {
            throw new SQLException("Get init table sql error", e);
        }
    }
}
