package air.kanna.mystorage.sync.service;

import java.io.File;
import java.net.Socket;

import org.apache.log4j.Logger;

import air.kanna.mystorage.sync.model.ConnectParam;
import air.kanna.mystorage.sync.process.LocalFileReceiveSyncProcess;

public class SyncClient{
    private static final Logger logger = Logger.getLogger(SyncClient.class);
    
    private Socket socket;
    private LocalFileReceiveSyncProcess receiveProcess;

    public void start(ConnectParam param, File file){
        try {
            receiveProcess = new LocalFileReceiveSyncProcess(param, file);
            socket = new Socket(param.getIp(), param.getPort());
            logger.info("Connect to: " + param.getIp());
            
            receiveProcess.start(socket);
            
        }catch(Exception e) {
            if(receiveProcess != null && receiveProcess.isFinish()) {
                
            }else {
                logger.error("error", e);
            }
        }finally {
            logger.info("Client finish");
            if(receiveProcess != null && !receiveProcess.isFinish()) {
                try {
                    receiveProcess.finish();
                } catch (Exception e) {
                    logger.error("finish error", e);
                }
            }
        }
    }
}
