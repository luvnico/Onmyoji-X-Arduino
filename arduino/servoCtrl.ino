#include <Servo.h>

Servo servo;
int signal;
int LED = 13;
int DEGREE = 27.5;
int TAP = 240;

void setup() {
  // put your setup code here, to run once:
  pinMode(LED, OUTPUT);
  digitalWrite(LED, LOW);
  servo.attach(9);
  servo.write(0);
  delay(2000);
  Serial.begin(9600);
}

void loop() {
  // put your main code here, to run repeatedly:
  if(Serial.available() > 0){
    signal = Serial.read();
    if(signal == '1'){
      //连续四次点击模式
      //1 --> 红蛋出现
      servo.write(DEGREE);
      delay(TAP);
      servo.write(0);

      delay(1500);
      //2 --> 红蛋爆炸
      servo.write(DEGREE);
      delay(TAP);
      servo.write(0);

      delay(2200);
      //3 --> 结算完成
      servo.write(DEGREE);
      delay(TAP);
      servo.write(0);

      delay(2500);
      //4 --> 回到探索界面
      servo.write(DEGREE);
      delay(TAP);
      servo.write(0);
    }else if(signal == '2'){
      //单点测试模式
      servo.write(DEGREE);
      delay(TAP);
      servo.write(0);
    }
    
  }
}
