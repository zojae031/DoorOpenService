#include <SoftwareSerial.h>
#define BT_RX 8
#define BT_TX 9
#define SENSOR 6
#define OPEN 3
#define CLOSE 2
SoftwareSerial BlueTooth(BT_RX, BT_TX);
void setup() {
  BlueTooth.begin(9600);
  pinMode(OPEN, OUTPUT);
  pinMode(CLOSE, OUTPUT);
  pinMode(SENSOR,INPUT);
  digitalWrite(CLOSE,HIGH);
}


void loop() {
  if(BlueTooth.available()) {
    char data = BlueTooth.read(); // 블루투스로부터 데이터를 받아옴
    int walk;
    if (data == '1') { //만약 입력된 데이터가 1이면
      walk = d_open();
      if(walk==0)
      {
        BlueTooth.write(100);//기기에 1을 보냄
      }//지나가지 않은 경우
      else if(walk == 1)
      {
        BlueTooth.write(100);
      }//지나간 경우
    }
  }
}
int d_open()
{
  int count=0;
  unsigned long currenttime = millis();
  unsigned long finishtime = currenttime+10000;
  unsigned long savetime = currenttime+6000;
      
  digitalWrite(CLOSE,LOW);
  digitalWrite(OPEN,HIGH);
  
  while(digitalRead(SENSOR) != LOW)
  {
     if(finishtime - currenttime <= 5000)
    {
      if((digitalRead(OPEN)==HIGH) && (currenttime >= savetime))
      {
        digitalWrite(OPEN,LOW);
        savetime = currenttime+1000;
      }
      else if((digitalRead(OPEN)==LOW) && (currenttime >= savetime))
      {
        digitalWrite(OPEN,HIGH);
        savetime = currenttime+1000;
      }
    }
    if(finishtime < currenttime)
    {
      digitalWrite(OPEN,LOW);
      digitalWrite(CLOSE,HIGH);
      return 0;
    }
    currenttime = millis();  
  }
  digitalWrite(OPEN,LOW);
  digitalWrite(CLOSE,HIGH);
  return 1;
}
