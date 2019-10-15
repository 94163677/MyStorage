package air.kanna.mystorage.dao.impl.sqlite.init;

import java.io.File;
import java.sql.Connection;

public class SqliteInitializeTest {

    public static void main(String[] args) {
        File testDB = new File("D:\\MyGitSource\\MyStorage\\mystorage\\test2.db");
        SqliteInitialize init = new SqliteInitialize();
        
        try {
            Connection conn = init.initAndGetConnection(testDB);
            conn.close();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
