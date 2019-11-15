package air.kanna.mystorage.service.hash.impl;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.security.MessageDigest;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import air.kanna.mystorage.model.FileItem;
import air.kanna.mystorage.model.FileType;
import air.kanna.mystorage.service.hash.HashService;
import air.kanna.mystorage.util.NumberUtil;
import air.kanna.mystorage.util.StringUtil;

public class LocalMsgDigestHashServiceImpl implements HashService {

    private List<MessageDigest> digestList = new ArrayList<>();
    
    @Override
    public Map<String, String> getFileItemHashString(FileItem item) throws Exception{
        Map<String, String> hashResult = new HashMap<>();
        
        if(item == null || item.getDiskDescription() == null) {
            throw new NullPointerException("FileItem or its DiskDescription is null");
        }
        if(digestList.size() <= 0 || item.getFileTypeObj() != FileType.TYPE_FILE) {
            return hashResult;
        }
        
        File local = getLocalFile(item);
        if(local.exists() && local.isFile()) {
            
        }else {
            throw new IllegalArgumentException("File is not exists: " + local.getAbsolutePath());
        }
        
        InputStream ins = new FileInputStream(local);
        byte[] buffer = new byte[1024 * 1024];
        int length = -1;
        
        for(MessageDigest digest : digestList) {
            digest.reset();
        }
        
        for(length=ins.read(buffer); length > 0; length=ins.read(buffer)) {
            for(MessageDigest digest : digestList) {
                digest.update(buffer, 0, length);
            }
        }
        ins.close();
        
        for(MessageDigest digest : digestList) {
            hashResult.put(digest.getAlgorithm(), NumberUtil.toHexString(digest.digest()));
        }
        return hashResult;
    }
    
    public List<MessageDigest> getMessageDigestList(){
        return digestList;
    }
    
    public void addMessageDigest(MessageDigest added) {
        if(added == null) {
            return;
        }
        for(MessageDigest digest : digestList) {
            if(added.getAlgorithm().equals(digest.getAlgorithm())) {
                return;
            }
        }
        digestList.add(added);
    }
    
    private File getLocalFile(FileItem item) {
        if(StringUtil.isSpace(item.getDiskDescription().getBasePath()) 
                || StringUtil.isSpace(item.getFilePath())) {
            throw new IllegalArgumentException("FileItem's path or its DiskDescription's path is null");
        }
        String path = item.getDiskDescription().getBasePath();
        if(path.endsWith(File.separator)) {
            if(item.getFilePath().startsWith(File.separator)) {
                path += item.getFilePath().substring(File.separator.length());
            }else {
                path += item.getFilePath();
            }
        }else {
            if(item.getFilePath().startsWith(File.separator)) {
                path += item.getFilePath();
            }else {
                path = path + File.separator + item.getFilePath();
            }
        }
        return new File(path);
    }
}
