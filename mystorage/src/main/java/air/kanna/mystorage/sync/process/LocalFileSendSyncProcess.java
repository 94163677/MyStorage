package air.kanna.mystorage.sync.process;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import air.kanna.mystorage.sync.model.ConnectParam;
import air.kanna.mystorage.sync.model.FileData;
import air.kanna.mystorage.sync.model.FileInformation;
import air.kanna.mystorage.sync.model.OperMessage;
import air.kanna.mystorage.util.NumberUtil;

public class LocalFileSendSyncProcess extends BaseSyncProcess{
    private static final Logger logger = Logger.getLogger(LocalFileSendSyncProcess.class);
    private static final int DEFAULT_BLOCK_SIZE = 50 * 1024;//默认块大小：50KB
    
    private File baseFile;
    
    public LocalFileSendSyncProcess(ConnectParam param, File file) {
        super(param);
        if(file == null) {
            throw new NullPointerException("Send or Receive File is null");
        }
        if(file.isFile()) {
            if(!file.exists()) {
                throw new IllegalArgumentException("Send file not exists");
            }
            baseFile = file;
        }else{
            throw new IllegalArgumentException("Unknow file: " + file.getAbsolutePath());
        }
    }

    

    @Override
    protected void doStart(OperMessage msg) throws Exception {
        InputStream ins = new BufferedInputStream(new FileInputStream(baseFile), (10 * DEFAULT_BLOCK_SIZE));
        FileInformation info = new FileInformation();
        OperMessage message = new OperMessage();
        MessageDigest digest = MessageDigest.getInstance("MD5");
        
        byte[] buffer = new byte[DEFAULT_BLOCK_SIZE];
        int fileId = (int)(Math.random() * Integer.MAX_VALUE);
        int readed = -1, count = 1;
        
        info.setFileId(fileId);
        info.setFileSize(baseFile.length());
        info.setDataSize(DEFAULT_BLOCK_SIZE);
        info.setFileName(baseFile.getName());
        info.setFileHash("");
        
        message.setMessageType(OperMessage.MSG_DATA);
        message.setMessage(FileInformation.class.getName() + JSON.toJSONString(info));
        
        sendMessage(message);
        
        message = new OperMessage();
        message.setMessageType(OperMessage.MSG_DATA);
        
        for(readed=ins.read(buffer); readed>0; readed=ins.read(buffer), count++) {
            FileData data = new FileData();
            
            data.setFileId(info.getFileId());
            data.setDataNum(count);
            data.setData(NumberUtil.toHexString(buffer));
            digest.update(buffer, 0, readed);
            
            message.setMessage(FileData.class.getName() + JSON.toJSONString(data));
            
            sendMessage(message);
        }
        ins.close();
        
        info.setFileHash(NumberUtil.toHexString(digest.digest()));
        message.setMessage(FileInformation.class.getName() + JSON.toJSONString(info));
        
        sendMessage(message);
    }

    @Override
    protected void doData(OperMessage msg) throws Exception {}
}
