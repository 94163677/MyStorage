package air.kanna.mystorage.sync.process;

import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.security.MessageDigest;

import air.kanna.mystorage.sync.model.FileInformation;

public class FileInforProcess extends FileInformation {

    private MessageDigest checkDigest;
    private OutputStream outStream;
    private int maxBlock;
    private int lastBlockSize;
    
    public FileInforProcess(FileInformation infor) {
        if(infor == null) {
            throw new NullPointerException("FileInformation is null");
        }
        setFileId(infor.getFileId());
        setFileSize(infor.getFileSize());
        setDataSize(infor.getDataSize());
        setFileName(infor.getFileName());
        setFileHash(infor.getFileHash());
        
        maxBlock = (int)(infor.getFileSize() / infor.getDataSize()) + 1;
        lastBlockSize = (int)(infor.getFileSize() % infor.getDataSize());
    }

    public MessageDigest getCheckDigest() {
        return checkDigest;
    }

    public OutputStream getOutStream() {
        return outStream;
    }

    public void setCheckDigest(MessageDigest checkDigest) {
        this.checkDigest = checkDigest;
    }

    public void setOutStream(OutputStream outStream) {
        this.outStream = outStream;
    }

    public int getMaxBlock() {
        return maxBlock;
    }

    public int getLastBlockSize() {
        return lastBlockSize;
    }
}
