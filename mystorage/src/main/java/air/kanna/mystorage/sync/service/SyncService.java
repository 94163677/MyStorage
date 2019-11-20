package air.kanna.mystorage.sync.service;

import java.io.InputStream;
import java.net.ServerSocket;
import java.net.Socket;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import air.kanna.mystorage.sync.model.ConnectParam;
import air.kanna.mystorage.sync.model.OperMessage;
import air.kanna.mystorage.sync.process.BaseSyncProcess;
import air.kanna.mystorage.util.NumberUtil;
import air.kanna.mystorage.util.StringUtil;

public class SyncService{
    private static final Logger logger = Logger.getLogger(SyncService.class);
    
    private ServerSocket service;
    private BaseSyncProcess serviceProcess;
    private ConnectParam param;
    
    public SyncService(ConnectParam param) {
//        super(param);
    }
    
    public void start() throws Exception{
        service = new ServerSocket(param.getPort());
        Socket socket = service.accept();
        logger.info("Connect with: " + socket.getInetAddress().getHostAddress());
    }
    
    
}
