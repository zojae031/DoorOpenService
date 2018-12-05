#include <SoftwareSerial.h>
#include <Stepper.h>
#define BT_RX 8
#define BT_TX 9
#define SENSOR 6

#define OPEN 2
#define CLOSE 3

#define STEP1 10
#define STEP2 12
#define STEP3 11
#define STEP4 13
const int steps = 64;// 모터별 스탭 수
//BLUETOOTH RX -> TX , TX-> RX;
//SENSOR = 적외선 센서
//OPEN,CLOSE,WAIT 3개의 output;
//Stepper 의 값이 10,12,11,13 인 이유 : 4선식의 경우 전류의 방향이 바뀌지 않는 특징으로 Stepper의 파라미터중 1은 A, 2는 B, 3은 A`,4은 B` 형식으로 되어있다. A, B에 전류가 흐르는경우 좌측 1스탭, A`,B`에 전류가 흐르는경우 우측 1스탭
//유니폴라 스탭모터 1 step = 5.625도
SoftwareSerial BlueTooth(BT_RX, BT_TX);
Stepper stepper(steps,STEP1,STEP2,STEP3,STEP4); 
void setup() {
	BlueTooth.begin(9600);
	pinMode(OPEN, OUTPUT);
	pinMode(CLOSE, OUTPUT);
	pinMode(SENSOR,INPUT);
	digitalWrite(CLOSE,HIGH);
	stepper.setSpeed(220);//최고 속도
	pinMode(STEP1,OUTPUT);
	pinMode(STEP2,OUTPUT);
	pinMode(STEP3,OUTPUT); 
	pinMode(STEP4,OUTPUT);
	//none block을 위한 pinmode 변경
}


void loop() {
	if(BlueTooth.available()) {
		char data = BlueTooth.read(); // 블루투스로부터 데이터를 받아옴
		int walk;
		if (data == '1') 
		{ //만약 입력된 데이터가 1이면
			walk = d_open();
			switch(walk)//차후 지나간것과 지나가지 않았을때 보내는 데이터가 다를 것을 고려 한 Switch case 문
			{
				case 0:
					BlueTooth.write(100);//기기에 1을 보냄
				case 1:
					BlueTooth.write(100);//지나간 경우
			}
		}
	}
}
int d_open()
{
	int count=0;
	unsigned long currenttime = millis();
	unsigned long finishtime = currenttime+10000;
	unsigned long savetime = currenttime+6000;
//nonblock system 을 위해서 만든 millis 를 통한 시간 측정
	stepper.step(-512);//1024 = 180도 512 90도 
	digitalWrite(CLOSE,LOW);
	digitalWrite(OPEN,HIGH);
	while(digitalRead(SENSOR) != LOW)
	{
		if(finishtime - currenttime <= 5000)//5초를 지나게 되면 불이 깜빡이게끔 만든 부분
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
			stepper.step(512);
			return 0;
		}
		currenttime = millis();  
	}
	digitalWrite(OPEN,LOW);
	digitalWrite(CLOSE,HIGH);
	stepper.step(512);
	return 1;
}
