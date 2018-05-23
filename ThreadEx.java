/**
 * @title Onmyoji X Arduino [CLI]
 * @date May 21st, 2018
 * @author Luvnico
 * @environment: OS X 10.12, jdk 1.8
 * Libraries:
 *  - jSerialComm-2.0.2
 *  - opencv-3.4.1 w/ extra modules (xfeatures2d)
 */

/*
 *  - instructions for opencv installation (Apache Ant, CMake required):
 *      - download opencv extended modules from official Git repo
 *      - open CMake GUI
 *          -> configure -> find name 'OPENCV_EXTRA_MODULES_PATH'
 *          -> set value to your extra modules directory (e.g. '/Users/../opencv-3.4.1/extra_modules/modules')
 *          -> configure -> generate -> done
 * Software:
 *  - Quicktime Player
 * Hardware:
 *  - Arduino UNO R3
 *  - a micro servo
 *  - a stylus pen
 */
package OnmyojiHelper;

import com.fazecast.jSerialComm.*;
import java.io.IOException;
import java.io.OutputStream;

public class ThreadEx extends Thread {

    public static SerialPort[] ports = SerialPort.getCommPorts();
    public static SerialPort port = null;
    public static OutputStream oStream = null;

    public ThreadEx(){
        System.out.println("Available ports: ");
        for(SerialPort p: ports){
            System.out.println(p.getSystemPortName());
            if(p.getSystemPortName().equals("cu.usbmodem1411")){
                port = p;
            }
        }
        try{
            port.openPort();
            oStream = port.getOutputStream();
        }catch(Exception e){
            System.err.println("Failed to open port: "+e);
            return;
        }

    }


    public static int i = 0;
    /*御魂配置 ～ 50s一把左右 */
    int PERIOD = 50*1000;

    /*觉醒配置 ～ 20s一把左右 */
    //int PERIOD = 20*1000;

    int ROUNDS = 30;

    /*
    * Notify Arduino Servo to touch the screen
    * */
    public void touch(){
        try {
            byte[] data = "1".getBytes();
            oStream.write(data);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void run() {
        while(i < ROUNDS){
            try {
                System.out.println("[[Round: "+i+"]]");
                //todo ...
                String win = "Win.jpg";
                String lose = "Lose.jpg";
                String capture = ScreenCapture.capture();

                int res_w = ScreenCapture.compareFeature(win, capture); //matches for win
                int res_l = ScreenCapture.compareFeature(lose, capture); //matches for lose

                if (res_w >= 100) {
                    System.out.println("-- You won. Sending signal to Arduino...");
                    //notify Arduino to tap screen
                    touch();
                } else {
                    if(res_l >= 100){
                        System.out.println("-- You lost.");
                        Thread.currentThread().interrupt();
                    }
                    else{
                        System.out.println("No match found. Continue matching every other second ... (up to 30 secs)");
                        try {
                            int j = 0; //loop for at most 30 secs to find a match for win/lose
                            do{
                                Thread.sleep(1000);
                                capture = ScreenCapture.capture();
                                System.out.println("["+j+"]: screenshot captured");
                                res_w = ScreenCapture.compareFeature(win, capture); //matches for win
                                res_l = ScreenCapture.compareFeature(lose, capture); //matches for lose
                                j++;
                            }
                            while(res_l < 100 && res_w < 100 && j < 30);

                            if(res_w >= 100){
                                System.out.println("-- You won. Sending signal to Arduino ...");
                                touch();
                            }
                            else if(res_l >= 100){
                                System.out.println("-- You lost.");
                                Thread.currentThread().interrupt();
                            }
                            else{
                                System.out.println("-- Matching timeout.");
                                Thread.currentThread().interrupt();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }

                Thread.sleep(PERIOD);
                i++;
            } catch (InterruptedException ex) {
                // code to resume or terminate...
                System.err.println("Thread Interrupted ...");
                return;
            }
        }

    }

    public static void main(String[] args) {

        ThreadEx t1 = new ThreadEx();

        t1.start();
        try {
            t1.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

        port.closePort();
    }

}





