package air.kanna.mystorage.sync.process;

import java.net.Socket;
import java.security.MessageDigest;

import air.kanna.mystorage.sync.model.ConnectParam;
import air.kanna.mystorage.sync.model.OperMessage;
import air.kanna.mystorage.sync.process.BaseSyncProcess;
import air.kanna.mystorage.sync.util.NetworkUtil;
import air.kanna.mystorage.util.NumberUtil;

public class TestBaseSyncConnect {

    public static void main(String[] args) {
        BaseSyncProcess conn = new BaseSyncProcess(createConnectParam()) {
            @Override
            public void start(Socket socket) throws Exception{
                OperMessage msg = new OperMessage();
                msg.setMessageType(OperMessage.MSG_DATA);
                msg.setMessage("年审杀了对方");
                try {
                    String temp = getMessageString(msg);
                    System.out.println(temp);
                    OperMessage other = getMessage(temp);
                    System.out.println(msg);
                    System.out.println(other);
                }catch(Exception e) {
                    e.printStackTrace();
                }
            }
            @Override
            protected void doStart(OperMessage msg) throws Exception {}

            @Override
            protected void doData(OperMessage msg) throws Exception {}
        };
        
        try {
            conn.start(null);
        }catch(Exception e) {
            e.printStackTrace();
        }
    }

    private static ConnectParam createConnectParam() {
        double randNum = Math.random();
        long time = System.currentTimeMillis();
        ConnectParam param = new ConnectParam();
        
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            String temp = "" + time + randNum;
            
            digest.update(temp.getBytes());

            param.setIp(NetworkUtil.getLocalIpv4Address().get(0).getHostAddress());
            param.setPort((int)((Math.random() * 50000) + 10000));
            param.setKey(NumberUtil.toHexString(digest.digest()));
            
            return param;
        }catch(Exception e) {
            e.printStackTrace();
        }
        return null;
    }
}
