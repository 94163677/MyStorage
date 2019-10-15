package air.kanna.mystorage.dao.impl.sqlite;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.Statement;

public class TestSqlite {

    public static void main(String[] args) {
        try {
            InputStream ins = TestSqlite.class.getResourceAsStream("/air/kanna/mystorage/db/db_init.sql");
            BufferedReader reader = new BufferedReader(new InputStreamReader(ins, "UTF-8"), 10240);
            StringBuilder sb = new StringBuilder();
            String line = null;
            
            for(line = reader.readLine(); line != null; line = reader.readLine()) {
                sb.append(line).append('\n');
            }
            
            System.out.println(sb.toString());
            
            Class.forName("org.sqlite.JDBC");
            Connection conn = DriverManager.getConnection("jdbc:sqlite:D:\\MyGitSource\\MyStorage\\mystorage\\test.db");
            Statement stat = conn.createStatement();
            
            boolean success = stat.execute(sb.toString());
            System.out.println("create table: " + success);
            
            if(!conn.getAutoCommit()) {
                conn.commit();
            }
            stat.close();
            conn.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }
}
