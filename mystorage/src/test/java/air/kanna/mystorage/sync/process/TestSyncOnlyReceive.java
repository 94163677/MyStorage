package air.kanna.mystorage.sync.process;

import java.net.Socket;

import air.kanna.mystorage.sync.model.ConnectParam;
import air.kanna.mystorage.sync.model.OperMessage;

public class TestSyncOnlyReceive {

    public static void main(String[] args) {
        ConnectParam param = new ConnectParam();
        param.setIp("localhost");
        param.setPort(11543);
        param.setKey("1B612F2BE4B2536A4E22F6F7A1B84F3E");
        
        BaseSyncProcess process = new BaseSyncProcess(param) {
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
            protected void doStart(OperMessage msg) throws Exception {
                
            }
            @Override
            protected void doData(OperMessage msg) throws Exception {
                System.out.println(System.currentTimeMillis() + " receive data");
            }
        };
        
        try {
            Socket socket = new Socket(param.getIp(), param.getPort());
            process.start(socket);
        }catch(Exception e) {
            e.printStackTrace();
        }
        

    }

}
