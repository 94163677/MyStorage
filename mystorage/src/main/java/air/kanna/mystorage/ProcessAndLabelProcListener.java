package air.kanna.mystorage;

import javax.swing.JLabel;
import javax.swing.JProgressBar;

import air.kanna.kindlesync.ProcessListener;

public class ProcessAndLabelProcListener implements ProcessListener {

    private JLabel processText;
    private JProgressBar processBar;
    
    private int current = 0;
    private int max = 100;
    
    public ProcessAndLabelProcListener(
            JLabel processText, JProgressBar processBar) {
        if(processText == null || processBar == null) {
            throw new NullPointerException("JLabel or JProgressBar is null");
        }
        this.processText = processText;
        this.processBar = processBar;
    }
    
    @Override
    public void setMax(int max) {
        if(max <= 0){
            throw new IllegalArgumentException("ProcessBar's max must > 0");
        }
        this.max = max;
        this.current = 0;
        
        processBar.setMaximum(max);
        processBar.setMinimum(0);
        processBar.setValue(0);
        processText.setText("--");
    }

    @Override
    public void next(String message) {
        current++;
        if(current > max){
            processBar.setValue(max);
        }else{
            processBar.setValue(current);
        }
        processText.setText(message);
    }

    @Override
    public void setPosition(int current, String message) {
        if(current <= 0){
            processBar.setValue(0);
            this.current = 0;
        }else
        if(current > max){
            processBar.setValue(max);
            this.current = max;
        }else{
            processBar.setValue(current);
            this.current = current;
        }
        processText.setText(message);
    }

    @Override
    public void finish(String message) {
        processBar.setValue(max);
        processText.setText(message);
    }
}
