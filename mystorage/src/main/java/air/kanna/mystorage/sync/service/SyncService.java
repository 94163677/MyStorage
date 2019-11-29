package air.kanna.mystorage.sync.service;

import java.io.File;
import java.io.IOException;
import java.net.ServerSocket;
import java.net.Socket;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

import air.kanna.mystorage.sync.model.ConnectParam;
import air.kanna.mystorage.sync.process.LocalFileSendSyncProcess;

public class SyncService{
    private static final Logger logger = Logger.getLogger(SyncService.class);
    
    private ServerSocket service;
    private boolean isFinish = false;
    private List<LocalFileSendSyncProcess> processList = new ArrayList<>();

    public void start(ConnectParam param, File file){
        try {
            service = new ServerSocket(param.getPort());
            logger.info("Service Started");
            
            for(;!isFinish;) {
                Socket socket = service.accept();
                if(socket == null) {
                    continue;
                }
                logger.info("Connect with: " + socket.getInetAddress().getHostAddress());
                LocalFileSendSyncProcess sendProcess = new LocalFileSendSyncProcess(param, file);
                processList.add(sendProcess);
                
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
                finish();
            }
        }
    }
    
    public void finish() {
        isFinish = true;
        for(LocalFileSendSyncProcess process : processList) {
            if(process == null || process.isFinish()) {
                continue;
            }
            try {
                process.finish();
            } catch (Exception e) {
                logger.error("finish error", e);
            }
        }
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
