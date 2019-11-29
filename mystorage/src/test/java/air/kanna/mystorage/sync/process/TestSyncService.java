package air.kanna.mystorage.sync.process;

import java.io.File;

import air.kanna.mystorage.sync.model.ConnectParam;
import air.kanna.mystorage.sync.service.SyncService;

public class TestSyncService {

    public static void main(String[] args) {
        ConnectParam param = new ConnectParam();
        File file = new File("D:\\迅雷下载\\CuteMIDI.zip");
        
        param.setIp("localhost");
        param.setPort(11543);
        param.setKey("1B612F2BE4B2536A4E22F6F7A1B84F3E");
        try {
            SyncService service = new SyncService();
            Thread thread = new Thread() {
                public void run() {
                    service.start(param, file);
                }
            };
            thread.start();
        }catch(Exception e) {
            e.printStackTrace();
        }

    }

}
