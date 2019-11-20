package air.kanna.mystorage.sync.process;

import java.io.File;

import air.kanna.mystorage.sync.model.ConnectParam;
import air.kanna.mystorage.sync.service.SyncClient;

public class TestSyncClient {

    public static void main(String[] args) {
        ConnectParam param = new ConnectParam();
        File path = new File("D:\\temp");
        
        param.setIp("localhost");
        param.setPort(11543);
        param.setKey("1B612F2BE4B2536A4E22F6F7A1B84F3E");
        try {
            SyncClient client = new SyncClient();
            Thread thread = new Thread() {
                public void run() {
                    client.start(param, path);
                }
            };
            thread.start();
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

}
