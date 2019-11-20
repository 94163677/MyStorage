package air.kanna.mystorage.sync.service;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;

import org.apache.log4j.Logger;

import air.kanna.mystorage.sync.model.ConnectParam;
import air.kanna.mystorage.sync.process.LocalFileSendSyncProcess;

public class SyncService{
    private static final Logger logger = Logger.getLogger(SyncService.class);
    
    private ServerSocket service;
    private LocalFileSendSyncProcess sendProcess;
    private boolean isFinish = false;

    public void start(ConnectParam param, File file){
        try {
            sendProcess = new LocalFileSendSyncProcess(param, file);
            service = new ServerSocket(param.getPort());
            logger.info("Service Started");
            
            for(;!isFinish;) {
                Socket socket = service.accept();
                if(socket == null) {
                    continue;
                }
                logger.info("Connect with: " + socket.getInetAddress().getHostAddress());
                
                Thread thread = new Thread() {
                    @Override
                    public void run() {
                        try {
                            sendProcess.start(socket);
                        } catch (Exception e) {
                            logger.error("process error", e);
                        }
                    }
                };
                thread.start();
            }
        }catch(Exception e) {
            if(!isFinish) {
                logger.error("error", e);
            }
        }finally {
            logger.info("Service finish");
            if(!isFinish) {
                if(sendProcess != null) {
                    try {
                        sendProcess.finish();
                    } catch (Exception e) {
                        logger.error("finish error", e);
                    }
                }
                finish();
            }
        }
    }
    
    public void finish() {
        isFinish = true;
        if(service != null) {
            try {
                service.close();
            } catch (IOException e) {
                logger.error("close error", e);
            }
        }
    }
    
    public boolean isFinish() {
        return isFinish;
    }
}
