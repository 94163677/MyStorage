package air.kanna.mystorage.sync.process;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;

import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;

import air.kanna.mystorage.sync.model.ConnectParam;
import air.kanna.mystorage.sync.model.OperMessage;
import air.kanna.mystorage.util.NumberUtil;
import air.kanna.mystorage.util.StringUtil;

public abstract class BaseSyncProcess {
    private static final Logger logger = Logger.getLogger(BaseSyncProcess.class);
    
    protected static final int KEY_LENGTH = 128 / 8;//默认128位
    protected static final String ALGORITHM = "AES";
    //如果用CBC方式，需要添加IV，不然解密会报错
    protected static final String CIPHER_MODE = "AES/ECB/PKCS5PADDING";
    protected static final String CHARSET = "UTF-8";
    
    protected ConnectParam param;
    protected Socket socket;
    protected byte[] key;
    protected boolean isBreak = false;
    
    protected abstract void doStart(OperMessage msg) throws Exception;
    protected abstract void doData(OperMessage msg) throws Exception;
    
    protected BaseSyncProcess(ConnectParam param) {
        if(param == null) {
            throw new NullPointerException("ConnectParam is null");
        }
        if(param.getPort() < 10000) {
            throw new IllegalArgumentException("ConnectParam's port error: " + param.getPort());
        }
        if(StringUtil.isSpace(param.getIp())) {
            throw new NullPointerException("ConnectParam's ip is null");
        }
        if(StringUtil.isSpace(param.getKey())) {
            throw new NullPointerException("ConnectParam's key is null");
        }
        key = NumberUtil.fromHexString(param.getKey());
        if(key == null || key.length != KEY_LENGTH) {
            throw new IllegalArgumentException("ConnectParam's key error: " + param.getKey());
        }
        this.param = param;
    }
    
    public void start(Socket socket) throws Exception{
        if(socket == null || socket.isClosed()) {
            throw new IllegalArgumentException("Socket is null or closed");
        }
        isBreak = false;
        this.socket = socket;
        
        dealInput(socket.getInputStream());
        socket.close();
    }
    
    protected void finish() throws Exception{
        OperMessage reply = new OperMessage();

        reply.setMessageType(OperMessage.MSG_END);
        reply.setMessage("");
        
        sendMessage(reply);
        socket.close();
    }
    
    private void dealInput(InputStream ins) throws Exception{
        StringBuilder sb = new StringBuilder();
        int ch = -1;
        
        for(ch=ins.read(); ((!isBreak) && ch>=0); ch=ins.read()) {
            if(ch == 0) {
                dealMessage(getMessage(sb.toString()));
                sb = new StringBuilder();
            }else {
                sb.append((char)ch);
            }
        }
        if(sb.length() > 0) {
            dealMessage(getMessage(sb.toString()));
        }
    }

    private void dealMessage(OperMessage msg) throws Exception{
        if(msg == null) {
            return;
        }
        switch(msg.getMessageType()) {
            case OperMessage.MSG_CONNECT: doConnect(msg);break;
            case OperMessage.MSG_READY: doReady(msg);break;
            case OperMessage.MSG_START: doStart(msg);break;
            case OperMessage.MSG_DATA: doData(msg);break;
            case OperMessage.MSG_END: doEnd(msg);break;
            default: logger.error("Cannot process message: " + msg);
        }
    }
    
    private void doConnect(OperMessage msg) throws Exception{
        OperMessage reply = new OperMessage();

        reply.setMessageType(OperMessage.MSG_READY);
        reply.setMessage("");
        
        sendMessage(reply);
    }
    
    private void doReady(OperMessage msg) throws Exception{
        OperMessage reply = new OperMessage();

        reply.setMessageType(OperMessage.MSG_START);
        reply.setMessage("");
        
        sendMessage(reply);
    }
    
    private void doEnd(OperMessage msg) throws Exception{
        isBreak = true;
    }
    
    protected void sendMessage(OperMessage msg) throws Exception{
        if(msg == null) {
            return;
        }
        OutputStream out = socket.getOutputStream();
        
        out.write(getMessageString(msg).getBytes());
        out.write(0);
        out.flush();
    }
    
    protected OperMessage getMessage(String data) throws Exception{
        if(StringUtil.isSpace(data)) {
            return null;
        }
        // 1 获取解密密钥
        SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
        // 2 获取Cipher实例
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        // 3 初始化Cipher实例。设置执行模式为解密以及解密密钥
        cipher.init(Cipher.DECRYPT_MODE, keySpec);
        // 4 解密
        byte[] orgBytes = cipher.doFinal(NumberUtil.fromHexString(data));
        // 5 生成字符串
        String msgJson = new String(orgBytes, CHARSET);
        // 6 根据Json生成实体消息类
        return JSON.parseObject(msgJson, OperMessage.class);
    }
    
    protected String getMessageString(OperMessage msg) throws Exception{
        if(msg == null) {
            return "";
        }
        // 1 获取加密密钥
        SecretKeySpec keySpec = new SecretKeySpec(key, ALGORITHM);
        // 2 获取Cipher实例
        Cipher cipher = Cipher.getInstance(CIPHER_MODE);
        // 3 初始化Cipher实例。设置执行模式为加密以及加密密钥
        cipher.init(Cipher.ENCRYPT_MODE, keySpec);
        // 4 根据消息实体生成Json字符串
        String msgJson = JSON.toJSONString(msg);
        // 5 加密
        byte[] encBytes = cipher.doFinal(msgJson.getBytes(CHARSET));
        // 6 生成HEX字符串
        return NumberUtil.toHexString(encBytes);
    }
}
