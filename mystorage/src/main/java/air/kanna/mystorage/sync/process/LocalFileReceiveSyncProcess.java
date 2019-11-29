package air.kanna.mystorage.sync.process;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.net.Socket;
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

public class LocalFileReceiveSyncProcess extends BaseSyncProcess {
    private static final Logger logger = Logger.getLogger(LocalFileSendSyncProcess.class);
    
    public static final String TRANS_TEMP_FILE_END = ".tns";
    
    private File baseFile;
    private List<FileInforProcess> fileList = new ArrayList<>();
    
    public LocalFileReceiveSyncProcess(ConnectParam param, File file) {
        super(param);
        if(file == null) {
            throw new NullPointerException("Send or Receive File is null");
        }
        if(file.isDirectory()) {
            if(!file.exists()) {
                if(!file.mkdirs()) {
                    throw new IllegalArgumentException("Receive path cannot be creat");
                }
            }
            baseFile = file;
        }else{
            throw new IllegalArgumentException("Unknow file: " + file.getAbsolutePath());
        }
    }

    @Override
    public void start(Socket socket) throws Exception{
        if(socket == null || socket.isClosed()) {
            throw new IllegalArgumentException("Socket is null or closed");
        }
        isBreak = false;
        this.socket = socket;
        OperMessage reply = new OperMessage();

        reply.setMessageType(OperMessage.MSG_CONNECT);
        reply.setMessage("");
        sendMessage(reply);

        super.start(socket);
    }

    @Override
    protected void doStart(OperMessage msg) throws Exception {}

    @Override
    protected void doData(OperMessage msg) throws Exception {
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
            proc.getCheckDigest().update(data, 0, proc.getLastBlockSize());
            proc.getOutStream().write(data, 0, proc.getLastBlockSize());
        }else {
            proc.getCheckDigest().update(data);
            proc.getOutStream().write(data);
        }
        listener.setPosition(fileData.getDataNum(), null);
    }
    
    private void doNewInputFile(FileInformation fileInfo) throws Exception {
        FileInforProcess proc = new FileInforProcess(fileInfo);
        
        proc.setCheckDigest(MessageDigest.getInstance("MD5"));
        proc.setFileName(fileInfo.getFileName() + "." + fileInfo.getFileId() + TRANS_TEMP_FILE_END);
        
        File tranFile = new File(baseFile, proc.getFileName());
        if(tranFile.exists()) {
            if(!tranFile.delete()) {
                throw new RuntimeException("Cannot delete File" + tranFile.getAbsolutePath());
            }
        }
        proc.setOutStream(
                new BufferedOutputStream(
                        new FileOutputStream(tranFile), (10 * fileInfo.getDataSize())));
        
        listener.setMax(proc.getMaxBlock());
        listener.setPosition(0, fileInfo.getFileName());
        
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
        
        listener.finish(fileInfo.getFileName());
        
        if(fileList.size() <= 0) {
            isBreak = true;
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
