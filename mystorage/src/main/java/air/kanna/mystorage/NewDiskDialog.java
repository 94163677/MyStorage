package air.kanna.mystorage;

import java.awt.BorderLayout;
import java.awt.FlowLayout;
import java.awt.Frame;
import java.awt.GridLayout;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.io.File;
import java.util.Date;

import javax.swing.JButton;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JLabel;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JTextField;
import javax.swing.border.EmptyBorder;

import air.kanna.mystorage.model.DiskDescription;
import air.kanna.mystorage.util.DateTimeUtil;

public class NewDiskDialog extends JDialog {

    private final JPanel contentPanel = new JPanel();
    private JDialog dialog;
    private JButton okButton;
    private JButton cancelButton;
    private JTextField diskPathTf;
    private JButton selectDiskBtn;
    private JTextField diskDescTf;
    private JFileChooser chooser;
    
    private DiskDescription disk = null;

    /**
     * Launch the application.
     */
    public static void main(String[] args) {
        try {
            NewDiskDialog dialog = new NewDiskDialog();
            dialog.setDefaultCloseOperation(JDialog.DISPOSE_ON_CLOSE);
            dialog.setVisible(true);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Create the dialog.
     */
    public NewDiskDialog() {
        initPanel();
        initControl();
    }
    
    public NewDiskDialog(Frame owner) {
        super(owner);
        initPanel();
        initControl();
    }
    
    private void initPanel() {
        dialog = this;
        setTitle("新增磁盘");
        setBounds(100, 100, 450, 253);
        getContentPane().setLayout(new BorderLayout());
        contentPanel.setBorder(new EmptyBorder(5, 5, 5, 5));
        getContentPane().add(contentPanel, BorderLayout.CENTER);
        contentPanel.setLayout(new GridLayout(5, 1, 0, 0));
        
        JPanel panel01 = new JPanel();
        contentPanel.add(panel01);
        
        JPanel panel02 = new JPanel();
        contentPanel.add(panel02);
        JLabel lblNewLabel = new JLabel("磁盘路径：");
        panel02.setLayout(new FlowLayout(FlowLayout.LEADING));
        panel02.add(lblNewLabel);
        
        diskPathTf = new JTextField();
        panel02.add(diskPathTf);
        diskPathTf.setColumns(18);
        selectDiskBtn = new JButton("...");
        panel02.add(selectDiskBtn);
        
        JPanel panel03 = new JPanel();
        contentPanel.add(panel03);
        
        JPanel panel04 = new JPanel();
        contentPanel.add(panel04);
        JLabel lblNewLabel_1 = new JLabel("磁盘描述：");
        panel04.setLayout(new FlowLayout(FlowLayout.LEADING));
        panel04.add(lblNewLabel_1);
        
        diskDescTf = new JTextField();
        panel04.add(diskDescTf);
        diskDescTf.setColumns(20);
        
        
        JPanel panel05 = new JPanel();
        contentPanel.add(panel05);
        
        {
            JPanel buttonPane = new JPanel();
            buttonPane.setLayout(new FlowLayout(FlowLayout.RIGHT));
            getContentPane().add(buttonPane, BorderLayout.SOUTH);
            {
                okButton = new JButton("确定");
                okButton.setActionCommand("OK");
                buttonPane.add(okButton);
                getRootPane().setDefaultButton(okButton);
            }
            {
                cancelButton = new JButton("取消");
                cancelButton.setActionCommand("Cancel");
                buttonPane.add(cancelButton);
            }
        }
        
        chooser = new JFileChooser();
        chooser.setDialogType(JFileChooser.OPEN_DIALOG);
        chooser.setDialogTitle("请选择目录");
        chooser.setApproveButtonText("选择该目录");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setMultiSelectionEnabled(false);
    }
    
    private void initControl() {
        selectDiskBtn.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                chooser.setSelectedFile(new File("."));
                if(JFileChooser.APPROVE_OPTION == chooser.showOpenDialog(dialog)){
                    File selected = chooser.getSelectedFile();
                    diskPathTf.setText(selected.getAbsolutePath());
                    diskDescTf.setText("");
                    disk = null;
                }
            }
        });
        
        cancelButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                disk = null;
                dispose();
            }
        });
        
        okButton.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent arg0) {
                File selected = new File(diskPathTf.getText());
                if(selected.isDirectory() && selected.exists()) {
                    disk = new DiskDescription();
                    disk.setBasePath(selected.getAbsolutePath());
                    disk.setDescription(diskDescTf.getText());
                    disk.setId(-1);
                    disk.setLastUpdate(DateTimeUtil.getDateTimeString(new Date()));
                    dispose();
                }else {
                    JOptionPane.showMessageDialog(dialog, "选择目录不是目录或者不存在", "错误", JOptionPane.ERROR_MESSAGE);
                }
            }
        });
        
    }
    
    public DiskDescription getDisk() {
        return disk;
    }

}
