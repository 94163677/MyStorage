package air.kanna.mystorage.sync.process;

import java.io.File;

import air.kanna.mystorage.sync.model.ConnectParam;
import air.kanna.mystorage.sync.service.SyncClient;
import air.kanna.mystorage.sync.service.SyncService;

public class TestLocalFileTransSyncProcess {

    public static void main(String[] args) {
        ConnectParam param = new ConnectParam();
        File file = new File("D:\\迅雷下载\\CuteMIDI.zip");
        File path = new File("D:\\temp");
        
        param.setIp("localhost");
        param.setPort(23456);
        param.setKey("C68121D1F3E5C2071E1A2076FEFCB789");
        try {
            SyncService service = new SyncService();
            SyncClient client = new SyncClient();
            new Thread() {
                public void run() {
                    service.start(param, file);
                }
            }.start();
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
