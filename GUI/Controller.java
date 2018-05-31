package OnmyojiHelper.GUI;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.*;
import com.fazecast.jSerialComm.*;
import javafx.scene.control.Button;
import javafx.scene.control.ToggleButton;
import javafx.event.ActionEvent;

import java.io.IOException;
import java.io.OutputStream;
import java.net.URL;
import java.text.NumberFormat;
import java.util.ResourceBundle;

public class Controller implements Initializable{
    String PORT_DEFAULT = "cu.usbmodem1411"; //default Arduino port for Mac OS X

    FxTask task = null;
    Thread th = null;
    SerialPort[] ports = null;
    SerialPort port = null;
    public static OutputStream oStream = null;
    int period = 1, rounds = 30; //run task for 30 rounds by default
    int timeout = 30; //match for another 30s for timeout by default
    Boolean isConnected = false;

    ObservableList<String> mode_choice = FXCollections.observableArrayList("御魂","觉醒","业原火","御灵");
    ObservableList<String> port_choice = FXCollections.observableArrayList();

    private void initPorts(){
        ports = SerialPort.getCommPorts();
        for(SerialPort p: ports)
            port_choice.add(p.getSystemPortName());
    }

    private void setPort(String portName){
        for(SerialPort p: ports){
            if(p.getSystemPortName().equals(portName)){
                port = p;
            }
        }
    }

    @FXML
    private CheckBox terminate_alert;
    @FXML
    private CheckBox finish_alert;
    @FXML
    private ChoiceBox mode_box;
    @FXML
    private ChoiceBox port_box;
    @FXML
    private ToggleButton port_conn_btn;
    @FXML
    private Button start_btn;
    @FXML
    private Button stop_btn;
    @FXML
    private TextField log;
    @FXML
    private ProgressBar bar;
    @FXML
    private TextField p; //period
    @FXML
    private TextField r; //rounds
    @FXML
    private TextField t; //timeout
    @FXML
    private Slider ps;
    @FXML
    private Slider rs;
    @FXML
    private Slider ts;

    @FXML
    public void tapTest(ActionEvent e){
        if(!isConnected){
            log.setText("请先连接Arduino端口！");
        }else{
            try {
                byte[] data = "2".getBytes();
                oStream.write(data);
            } catch (IOException ioe) {
                ioe.printStackTrace();
            }
        }
    }

    @FXML
    public void updateMode(ActionEvent e){
        String m = mode_box.getValue().toString();
        if(m.equals("御魂")){
            period = 50;
            timeout = 30;
        }else if(m.equals("觉醒")){
            period = 20;
            timeout = 20;
        }else if(m.equals("业原火")){
            period = 150;
            timeout = 120;
        }else{ //御灵模式
            period = 60;
        }
        p.setText(new Integer(period).toString());
        t.setText(new Integer(timeout).toString());
        log.setText("当前模式: "+m);
    }

    /*
    * Connect/Disconnect to Arduino port
    *   set isConnected to true upon success
    *   set the toggle button to be selected upon success
    * */
    @FXML
    public void connect(ActionEvent e){
        if(isConnected){
            port.closePort();
            isConnected = false;
            log.setText("已断开端口");
            port_conn_btn.setSelected(false);
            return;
        }

        if(port == null){
            //set port to the selected one
            try {
                setPort(port_box.getValue().toString());
            }catch(Exception ex){
                System.err.println("no port selected"+ex.getStackTrace());
                port_conn_btn.setSelected(false);
            }
        }

        try{
            port.openPort();
            oStream = port.getOutputStream();
            isConnected = true;
            log.setText("已成功连接端口");
            port_conn_btn.setSelected(true);
        }catch(Exception ex){
            log.setText("连接端口失败，请选择端口!");
        }
    }

    @FXML
    public void start_task(ActionEvent e){
        /*
        System.out.println("selected mode: "+mode_box.getValue());
        System.out.println("selected port: "+port_box.getValue());
        System.out.println("rounds: "+rounds);
        System.out.println("period: "+period);
        System.out.println("Terminate:"+terminate_alert.isSelected());
        System.out.println("Finish:"+finish_alert.isSelected());*/
        if(isConnected){
            System.out.println("Start thread");
            //disable checkboxes
            terminate_alert.setDisable(true);
            finish_alert.setDisable(true);
            //disable all choiceboxes, sliders and text fields
            mode_box.setDisable(true);
            port_box.setDisable(true);
            p.setDisable(true);
            r.setDisable(true);
            t.setDisable(true);
            ps.setDisable(true);
            rs.setDisable(true);
            ts.setDisable(true);

            //initiate a thread & pass the output stream
            task = new FxTask(oStream, rounds, period*1000, timeout,
                    terminate_alert.isSelected(), finish_alert.isSelected());

            bar.progressProperty().bind(task.progressProperty());
            log.textProperty().bind(task.messageProperty());

            th = new Thread(task);
            th.start();
        }
        else{
            log.setText("请先连接Arduino端口！");
            System.err.println("Please first connect to Arduino.");
        }
    }

    @FXML
    public void stop_task(ActionEvent e){

        if(task != null){
            task.cancel(true);
            //enable all checkboxes
            terminate_alert.setDisable(false);
            finish_alert.setDisable(false);
            //enable all choiceboxes, sliders and text fields
            mode_box.setDisable(false);
            port_box.setDisable(false);
            p.setDisable(false);
            r.setDisable(false);
            t.setDisable(false);
            ps.setDisable(false);
            rs.setDisable(false);
            ts.setDisable(false);
        }
        task = null;
    }

    @Override
    public void initialize(URL location, ResourceBundle resources){

        initPorts();
        setPort(PORT_DEFAULT);

        mode_box.setValue("御魂");
        mode_box.setItems(mode_choice);

        //set default value to the 3 textfields on left pane
        p.setText(new Integer(period).toString());
        r.setText(new Integer(rounds).toString());
        t.setText(new Integer(timeout).toString());

        //set default value to sliders
        ps.setValue(period);
        rs.setValue(rounds);
        ts.setValue(timeout);

        //set min value of sliders to be 1
        ps.setMin(1);
        rs.setMin(1);
        ts.setMin(1);
        //run 100 rounds at max
        rs.setMax(100);

        if(port != null)
            //set default port selection if Arduino detected
            port_box.setValue(port.getSystemPortName());

        port_box.setItems(port_choice);

        r.textProperty().bindBidirectional(rs.valueProperty(), NumberFormat.getNumberInstance());
        r.textProperty().addListener((observable, oldValue, newValue) -> {
            try{
                rounds = Integer.parseInt(newValue);
                if(rounds <= 0){
                    log.setText("局数必须大于零！");
                    rounds = 1;
                    r.setText("1");
                }
                else if(rounds > 100){
                    rounds = 100;
                    r.setText("100");
                }
                else
                    log.setText("局数: "+rounds+"局 (最多支持100局)");
            }catch(Exception ex){
                log.setText("局数有误，请输入数字！");
            }
        });

        p.textProperty().bindBidirectional(ps.valueProperty(), NumberFormat.getNumberInstance());
        p.textProperty().addListener((observableValue, oldValue, newValue) -> {
            try{
                period = Integer.parseInt(newValue);
                if(period <= 0){
                    log.setText("局数必须大于零！");
                    period = 1;
                    p.setText("1");
                }
                else
                    log.setText("每局间隔: "+period+"秒");
            }catch(Exception ex) {
                log.setText("间隔有误，请输入数字！");
            }
        });

        t.textProperty().bindBidirectional(ts.valueProperty(), NumberFormat.getNumberInstance());
        t.textProperty().addListener((observableValue, oldValue, newValue) -> {
            try{
                timeout = Integer.parseInt(newValue);
                if(timeout <= 0){
                    log.setText("超时检测时间必须大于零！");
                    timeout = 1;
                    t.setText("1");
                }
                else
                    log.setText("超时检测: "+timeout+"秒");
            }catch(Exception ex) {
                log.setText("间隔有误，请输入数字！");
            }
        });

        //set sliders to allow integer value only
        rs.valueProperty().addListener((obs, oldval, newVal) ->
                rs.setValue(newVal.intValue()));
        ps.valueProperty().addListener((obs, oldval, newVal) ->
                ps.setValue(newVal.intValue()));
        ts.valueProperty().addListener((obs, oldval, newVal) ->
                ts.setValue(newVal.intValue()));

    }
}
