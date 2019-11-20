package air.kanna.mystorage.sync;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.security.MessageDigest;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import air.kanna.mystorage.StartUp;
import air.kanna.mystorage.sync.model.ConnectParam;
import air.kanna.mystorage.sync.util.NetworkUtil;
import air.kanna.mystorage.util.NumberUtil;

public class SyncDBFileDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(SyncDBFileDialog.class);
    
    private final JPanel contentPanel = new JPanel();
    private JDialog dialog;
    private JLabel iconLB;
    private JLabel ipAddrLb;
    private JLabel portLB;
    private JLabel keyLb;
    
    private ConnectParam param;
    
    /**
     * Create the dialog.
     */
    public SyncDBFileDialog() {
        super();
        initPanel();
        initData();
    }
    public SyncDBFileDialog(Frame owner) {
        super(owner);
        initPanel();
        initData();
    }
    
    private void initData() {
        param = createConnectParam();
        if(param == null) {
            return;
        }
        
    }
    
    private ConnectParam createConnectParam() {
        double randNum = Math.random();
        long time = System.currentTimeMillis();
        ConnectParam param = new ConnectParam();
        
        try {
            MessageDigest digest = MessageDigest.getInstance("MD5");
            String temp = "" + time + randNum;
            
            digest.update(temp.getBytes());

            param.setIp(NetworkUtil.getLocalIpv4Address().get(0).getHostAddress());
            param.setPort(getRandomPort());
            param.setKey(NumberUtil.toHexString(digest.digest()));
            
            return param;
        }catch(Exception e) {
            logger.error("createConnectParam error.", e);
            JOptionPane.showMessageDialog(dialog, "生成链接参数错误，具体请看日志", "错误", JOptionPane.ERROR_MESSAGE);
            close();
        }
        return null;
    }
    
    private int getRandomPort() {
        return (int)((Math.random() * 50000) + 10000);
    }
    
    private void close() {
        dispose();
    }
    
    private void initPanel() {
        dialog = this;
        setTitle("数据同步");
        setBounds(100, 100, 450, 500);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new BorderLayout(0, 0));
        {
            iconLB = new JLabel("");
            contentPanel.add(iconLB, BorderLayout.CENTER);
        }
        {
            JPanel panel01 = new JPanel();
            panel01.setLayout(new FlowLayout(FlowLayout.LEFT));
            JLabel label01 = new JLabel("IP：");
            panel01.add(label01);
            contentPanel.add(panel01, BorderLayout.NORTH);
            {
                ipAddrLb = new JLabel("IP");
                panel01.add(ipAddrLb);
            }
            {
                JLabel label = new JLabel("  ");
                panel01.add(label);
            }
            {
                JLabel lblPort = new JLabel("PORT：");
                panel01.add(lblPort);
            }
            {
                portLB = new JLabel("PORT");
                panel01.add(portLB);
            }
            {
                JLabel label = new JLabel("  ");
                panel01.add(label);
            }
            {
                JLabel lblKey = new JLabel("KEY：");
                panel01.add(lblKey);
            }
            {
                keyLb = new JLabel("KEY");
                panel01.add(keyLb);
            }
            
        }
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                JButton cancelButton = new JButton("关闭");
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            SyncDBFileDialog dialog = new SyncDBFileDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
