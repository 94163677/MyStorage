package air.kanna.mystorage.sync.model;

public class OperMessage {
    //用于客户端首次链接服务端
    public static final String MSG_CONNECT = "CONNECT";
    //用户服务端应答客户端首次链接
    public static final String MSG_READY = "READY";
    //客户端发送服务端，表示可以接受数据
    public static final String MSG_START = "START";
    //客户端与服务端互传数据
    public static final String MSG_DATA = "DATA";
    //客户端或者服务端终止链接用的
    public static final String MSG_END = "END";
    
    private String messageType = MSG_CONNECT;
    private String message;
    private int id = 9;
    
    @Override
    public String toString() {
        return new StringBuilder()
                .append("msg: ")
                .append(messageType)
                .append('-')
                .append(message)
                .toString();
    }
    
    public int getId() {
        return id;
    }
    public void setId(int id) {
        this.id = id;
    }
    public String getMessageType() {
        return messageType;
    }
    public void setMessageType(String messageType) {
        this.messageType = messageType;
    }
    public String getMessage() {
        return message;
    }
    public void setMessage(String message) {
        this.message = message;
    }
}
