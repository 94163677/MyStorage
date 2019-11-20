package air.kanna.mystorage.sync.process;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.List;

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
    private boolean isSend = false;
    private List<FileInforProcess> fileList = new ArrayList<>();
    
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
            isSend = true;
        }
        if(file.isDirectory()) {
            if(!file.exists()) {
                if(!file.mkdirs()) {
                    throw new IllegalArgumentException("Receive path cannot be creat");
                }
            }
            baseFile = file;
        }
        throw new IllegalArgumentException("Unknow file: " + file.getAbsolutePath());
    }

    

    @Override
    protected void doStart(OperMessage msg) throws Exception {
        if(!isSend) {
            return;
        }
        OutputStream ous = socket.getOutputStream();
        InputStream ins = new BufferedInputStream(new FileInputStream(baseFile), (10 * DEFAULT_BLOCK_SIZE));
        FileInformation info = new FileInformation();
        OperMessage message = new OperMessage();
        
        byte[] buffer = new byte[DEFAULT_BLOCK_SIZE];
        int fileId = (int)(Math.random() * Integer.MAX_VALUE);
        int readed = -1, count = 1;
        
        info.setFileId(fileId);
        info.setFileSize(baseFile.length());
        info.setDataSize(DEFAULT_BLOCK_SIZE);
        info.setFileName(baseFile.getName());
        info.setFileHash("");
        
        message.setMessageType(OperMessage.MSG_DATA);
        message.setMessage(JSON.toJSONString(info));
        
        sendMessage(message);
        
        message = new OperMessage();
        message.setMessageType(OperMessage.MSG_DATA);
        
        for(readed=ins.read(buffer); readed>0; readed=ins.read(buffer)) {
            FileData data = new FileData();
            
            data.setFileId(info.getFileId());
            data.setDataNum(count);
            data.setData(NumberUtil.toHexString(buffer));
            message.setMessage(JSON.toJSONString(data));
            
            sendMessage(message);
        }
    }

    @Override
    protected void doData(OperMessage msg) throws Exception {
        if(isSend) {
            return;
        }
        String json = msg.getMessage();
        
        if(json.startsWith(FileInformation.class.getName())) {
            json = json.substring(FileInformation.class.getName().length());
            doFileInformation(JSON.parseObject(json, FileInformation.class));
        }else
        if(json.startsWith(FileData.class.getName())) {
            json = json.substring(FileData.class.getName().length());
            doFileData(JSON.parseObject(json, FileData.class));
        }
    }
    
    private void doFileInformation(FileInformation fileInfo) throws Exception {
        FileInforProcess proc = getFileInforProcess(fileInfo);
        if(proc == null) {
            doNewInputFile(fileInfo);
        }else {
            doFinishFile(proc, fileInfo);
        }
    }
    
    private void doFileData(FileData fileData) throws Exception {
        FileInforProcess proc = getFileInforProcess(fileData);
        if(proc == null) {
            logger.warn("Cannot found FileInforProcess with fileId: " + fileData.getFileId());
            return;
        }
        byte[] data = NumberUtil.fromHexString(fileData.getData());
        if(data.length != proc.getDataSize()) {
            throw new RuntimeException("FileData dataSize error: " + fileData.getData());
        }
        if(proc.getMaxBlock() <= fileData.getDataNum()) {
            proc.getOutStream().write(data, 0, proc.getLastBlockSize());
        }else {
            proc.getOutStream().write(data);
        }
    }
    
    private void doNewInputFile(FileInformation fileInfo) throws Exception {
        FileInforProcess proc = new FileInforProcess(fileInfo);
        
        proc.setCheckDigest(MessageDigest.getInstance("MD5"));
        proc.setFileName(fileInfo.getFileName() + "." + fileInfo.getFileId() + ".tns");
        
        File tranFile = new File(baseFile, proc.getFileName());
        if(tranFile.exists()) {
            if(!tranFile.delete()) {
                throw new RuntimeException("Cannot delete File" + tranFile.getAbsolutePath());
            }
        }
        proc.setOutStream(
                new BufferedOutputStream(
                        new FileOutputStream(tranFile), (10 * DEFAULT_BLOCK_SIZE)));
        fileList.add(proc);
    }
    
    private void doFinishFile(FileInforProcess proc, FileInformation fileInfo) throws Exception {
        String md5 = NumberUtil.toHexString(proc.getCheckDigest().digest());
        if(!md5.equalsIgnoreCase(fileInfo.getFileHash())) {
            throw new IllegalArgumentException("File hash not match, org: " + fileInfo.getFileHash() + ", dest: " + md5);
        }
        proc.getOutStream().flush();
        proc.getOutStream().close();
        
        File tranFile = new File(baseFile, proc.getFileName());
        File realFile = new File(baseFile, fileInfo.getFileName());
        
        if(!tranFile.renameTo(realFile)) {
            throw new RuntimeException("Cannot rename File from " + proc.getFileName() + ", to " + fileInfo.getFileName());
        }
        
        fileList.remove(proc);
        if(fileList.size() <= 0) {
            finish();
        }
    }
    
    private FileInforProcess getFileInforProcess(FileInformation fileInfo) {
        if(fileInfo == null) {
            return null;
        }
        for(FileInforProcess proc : fileList) {
            if(proc.getFileId() == fileInfo.getFileId()
                    && proc.getFileSize() == fileInfo.getFileSize()
                    && proc.getFileName().startsWith(fileInfo.getFileName())) {
                return proc;
            }
        }
        return null;
    }
    
    private FileInforProcess getFileInforProcess(FileData fileData) {
        if(fileData == null) {
            return null;
        }
        for(FileInforProcess proc : fileList) {
            if(proc.getFileId() == fileData.getFileId()) {
                return proc;
            }
        }
        return null;
    }
}
