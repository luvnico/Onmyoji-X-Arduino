package OnmyojiHelper.GUI;

import javafx.concurrent.Task;
import javafx.scene.media.AudioClip;

import java.io.IOException;
import java.io.OutputStream;

public class FxTask extends Task<Integer>{
    OutputStream oStream;
    int iteration = 0;
    int PERIOD, ROUNDS, TIMEOUT;
    boolean t_alert_required, f_alert_required;

    private void terminate_alert(){
        AudioClip t_sound = new AudioClip(this.getClass().getResource("sounds/fail.wav").toString());
        t_sound.play();
    }
    private void finish_alert(){
        AudioClip f_sound = new AudioClip(this.getClass().getResource("sounds/yonezu.mp3").toString());
        f_sound.play();
    }

    public FxTask(OutputStream o, int rounds, int period, int timeout, boolean t_requred, boolean f_required){
        this.oStream = o;
        this.PERIOD = period;
        this.ROUNDS = rounds;
        this.TIMEOUT = timeout;
        this.t_alert_required = t_requred;
        this.f_alert_required = f_required;
    }

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


    @Override
    protected Integer call(){
        while(iteration < ROUNDS){
            try {
                updateMessage("第"+(iteration+1)+"局");
                //System.out.println("[[Round: "+iteration+"]]");
                String win = "Win.jpg";
                String win_late = "Win_late.jpg";
                String lose = "Lose.jpg";
                String lose_sgl = "Lose_single.jpg";
                String capture = ImageProcess.capture();

                int res_w = Math.max(ImageProcess.compareFeature(win, capture), ImageProcess.compareFeature(win_late, capture)); //matches for win
                int res_l = Math.max(ImageProcess.compareFeature(lose, capture), ImageProcess.compareFeature(lose_sgl, capture)); //matches for lose

                if (res_w >= 100) {
                    updateMessage("胜利！正在发送信号...");
                    System.out.println("-- You won. Sending signal to Arduino...");
                    //notify Arduino to tap screen
                    touch();
                } else if(res_l >= 100){
                    updateMessage("失败！任务终止...");
                    System.out.println("-- You lost.");
                    cancel(true);
                }else{
                    updateMessage("没有找到匹配的结果，继续监听"+TIMEOUT+"秒...");
                    System.out.println("No match found. Continue matching every other second ... (up to "+TIMEOUT+" secs)");
                    try {
                        int j = 0; //loop for at most 30 secs to find a match for win/lose
                        do{
                            try{
                                Thread.sleep(1000);
                                capture = ImageProcess.capture();
                                updateMessage("["+(j+1)+"]: 截图匹配中 ...");
                                System.out.println("["+j+"]: screenshot captured");
                                //matches for win
                                res_w = Math.max(ImageProcess.compareFeature(win, capture), ImageProcess.compareFeature(win_late, capture));
                                //matches for lose
                                res_l = Math.max(ImageProcess.compareFeature(lose, capture), ImageProcess.compareFeature(lose_sgl, capture));
                                j++;
                            }catch(InterruptedException int_ex){
                                updateMessage("用户已中止任务！");
                                System.err.println("sleep interruption (terminated by stop btn).");
                                return -2;
                            }
                        }
                        while(res_l < 100 && res_w < 100 && j < TIMEOUT);

                        if(res_w >= 100){
                            updateMessage("胜利！正在发送信号...");
                            System.out.println("-- You won. Sending signal to Arduino ...");
                            touch();
                        }
                        else if(res_l >= 100){
                            updateMessage("失败！任务终止...");
                            System.out.println("-- You lost.");
                            cancel(true);
                        }
                        else{
                            System.out.println("-- Matching timeout.");
                            cancel(true);
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
                updateProgress(iteration+1, ROUNDS);
                if(iteration+1 < ROUNDS){
                    updateMessage("已完成"+(iteration+1)+"局，开启第"+(iteration+2)+"局...");
                }else{
                    updateMessage("全部任务已完成～");
                    if (f_alert_required)
                        finish_alert();
                }
                Thread.sleep(PERIOD);
                iteration++;
            } catch (InterruptedException ex) {
                // code to resume or terminate...
                System.err.println("sleep interruption (matching timeout)");
                updateMessage("结果匹配超时，任务已终止！");
                return -1;
            }
        }
        return 0;
    }


    @Override
    public boolean cancel(boolean mayInterruptIfRunning){
        updateProgress(iteration, ROUNDS);
        Thread.currentThread().interrupt();
        if(t_alert_required)
            terminate_alert();
        //notify in the log area (task cancelled)
        updateMessage("任务已取消");
        return super.cancel(mayInterruptIfRunning);
    }


}
