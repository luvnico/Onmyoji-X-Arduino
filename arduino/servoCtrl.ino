#include <Servo.h>

Servo servo;
int signal;
int LED = 13;
int DEGREE = 27.5;

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
      //1 --> 红蛋出现
      servo.write(DEGREE);
      delay(150);
      servo.write(0);

      delay(1500);
      //2 --> 红蛋爆炸
      servo.write(DEGREE);
      delay(150);
      servo.write(0);

      delay(2100);
      //3 --> 结算完成
      servo.write(DEGREE);
      delay(150);
      servo.write(0);

      delay(2400);
      //4 --> 回到探索界面
      servo.write(DEGREE);
      delay(150);
      servo.write(0);
    }
    
  }
}
