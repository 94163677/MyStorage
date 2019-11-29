package air.kanna.mystorage.sync;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Map;

import javax.swing.Icon;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.border.EmptyBorder;

import org.apache.log4j.Logger;

import com.alibaba.fastjson.JSON;
import com.google.zxing.BarcodeFormat;
import com.google.zxing.EncodeHintType;
import com.google.zxing.MultiFormatWriter;
import com.google.zxing.common.BitMatrix;
import com.google.zxing.qrcode.decoder.ErrorCorrectionLevel;

import air.kanna.mystorage.sync.model.ConnectParam;
import air.kanna.mystorage.sync.service.SyncService;
import air.kanna.mystorage.sync.util.NetworkUtil;
import air.kanna.mystorage.util.NumberUtil;

public class SyncDBFileDialog extends JDialog {
    private static final Logger logger = Logger.getLogger(SyncDBFileDialog.class);
    
    private final JPanel contentPanel = new JPanel();
    private JDialog dialog;
    private JLabel iconLb;
    private JLabel ipAddrLb;
    private JLabel portLb;
    private JLabel keyLb;
    
    private ConnectParam param;
    private File syncFile;
    private SyncService service;
    
    /**
     * Create the dialog.
     * @wbp.parser.constructor
     */
    public SyncDBFileDialog(File file) {
        super();
        syncFile = file;
        initPanel();
        initData();
        initControl();
    }
    public SyncDBFileDialog(File file, Frame owner) {
        super(owner);
        syncFile = file;
        initPanel();
        initData();
        initControl();
    }
    
    private void initControl() {
        addWindowListener(new WindowAdapter() {
            @Override
            public void windowClosed(WindowEvent e) {
                if(service != null) {
                    service.finish();
                    service = null;
                }
            }
            @Override
            public void windowClosing(WindowEvent e) {
                if(service != null) {
                    service.finish();
                    service = null;
                }
            }
        });
    }
    
    private void initData() {
        param = createConnectParam();
        if(param == null) {
            throw new RuntimeException("创建链接参数失败");
        }
        ipAddrLb.setText(param.getIp());
        portLb.setText(param.getPort() + "");
        keyLb.setText(sepString(param.getKey(), " "));
        logger.info(param.getKey());
        
        String json = JSON.toJSONString(param);
        
        iconLb.setText("");
        MultiFormatWriter multiFormatWriter = new MultiFormatWriter();
        Map hints = new HashMap();
        hints.put(EncodeHintType.CHARACTER_SET, "UTF-8");
        hints.put(EncodeHintType.ERROR_CORRECTION, ErrorCorrectionLevel.L);//纠错等级，从低到高为LMQH
        //hints.put(EncodeHintType.MARGIN, 2);//边距
        
        try {
            BitMatrix bitMatrix = multiFormatWriter.encode(json, BarcodeFormat.QR_CODE, 400, 400, hints);
            BufferedImage image = toBufferedImage(bitMatrix);
            
            Icon icon = new ImageIcon(image);
            iconLb.setIcon(icon);
        }catch(Exception e) {
            logger.error("Create qrcode error", e);
            iconLb.setText("创建二维码失败，请手动输入链接参数");
        }
        try {
            service = new SyncService();
            new Thread() {
                @Override
                public void run() {
                    service.start(param, syncFile);
                }
            }.start();
        }catch(Exception e) {
            logger.error("start service error", e);
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
    
    private BufferedImage toBufferedImage(BitMatrix matrix) {
        int width = matrix.getWidth();
        int height = matrix.getHeight();
        BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_RGB);
        
        for (int x = 0; x < width; x++) {
            for (int y = 0; y < height; y++) {
                image.setRGB(x, y, matrix.get(x, y) ? 0xFF000000 : 0xFFFFFFFF);
            }
        }
        return image;
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
            iconLb = new JLabel("");
            contentPanel.add(iconLb, BorderLayout.CENTER);
        }
        {
            JPanel panel01 = new JPanel();
            JPanel panel02 = new JPanel();
            JPanel panel03 = new JPanel();
            
            panel01.setLayout(new GridLayout(2, 1, 0, 0));
            panel02.setLayout(new FlowLayout(FlowLayout.LEFT));
            panel03.setLayout(new FlowLayout(FlowLayout.LEFT));
            
            panel01.add(panel02);
            panel01.add(panel03);
            
            JLabel label01 = new JLabel("IP：");
            panel02.add(label01);
            contentPanel.add(panel01, BorderLayout.NORTH);
            {
                ipAddrLb = new JLabel("IP");
                panel02.add(ipAddrLb);
            }
            {
                JLabel label = new JLabel("  ");
                panel02.add(label);
            }
            {
                JLabel lblPort = new JLabel("PORT：");
                panel02.add(lblPort);
            }
            {
                portLb = new JLabel("PORT");
                panel02.add(portLb);
            }
            {
                JLabel lblKey = new JLabel("KEY：");
                panel03.add(lblKey);
            }
            {
                keyLb = new JLabel("KEY");
                panel03.add(keyLb);
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
                cancelButton.addActionListener(new ActionListener() {
                    public void actionPerformed(ActionEvent arg0) {
                        dispose();
                    }
                });
            }
        }
    }
    
    private String sepString(String org, String sep) {
        StringBuilder sb = new StringBuilder();
        int sepNum = 4;
        
        for(int i=0, j=1; i<org.length(); i++, j++) {
            sb.append(org.charAt(i));
            if(j % sepNum == 0) {
                sb.append(sep);
            }
        }
        String result = sb.toString();
        if(result.endsWith(sep)) {
            result = sb.substring(0, (sb.length() - sep.length()));
        }
        return result;
    }

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            File[] files = new File(".").listFiles();
            File file = null;
            for(int i=0; i<files.length; i++) {
                if(files[i].isFile() && files[i].exists()) {
                    file = files[i];
                    break;
                }
            }
            SyncDBFileDialog dialog = new SyncDBFileDialog(file);
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
