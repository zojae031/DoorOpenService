# < Smart Leader ver1.0 (DoorOpenService) > 

### 시연영상 (앱 시연, 관리자 모드, 사용자 모드)
[<img src="./DoorOpenService/app/src/main/res/drawable/appicon.png" width="150" height="150">](https://youtu.be/GYMIdvY9oxQ)
[<img src="./DoorOpenService/app/src/main/res/drawable/appicon.png" width="150" height="150">](https://youtu.be/tJKp-b6CPmg)
[<img src="./DoorOpenService/app/src/main/res/drawable/appicon.png" width="150" height="150">](https://youtu.be/tJKp-b6CPmg)


## 개요 
##### 학교 출입문, 회사 출입문 등 결제기능없이 사용자 인증이 필요한 공간에 사용 할 수 있는 블루투스 기반 출입문 개폐 서비스

## System flow
#### 1.로그인 -> 2.GPS -> 3.ShakeAlgorithm -> 4.Bluetooth -> 5.OpenDoor -> 3.ShakeAlgorithm (반복)

## Framework
1. 안드로이드
2. JAVA Socket DB Server (Maria DB)
3. 아두이노 (출입문)


## 내용


### 1.Android
- 1Depth 의 사용자 인증 백그라운드 위주 서비스
`` 로그인 이후 사용자의 직접적인 Application 교류 X``
- GPS기반 위치 인증
``(등록된 장소에서만 ShakeAlgorithm이 동작한다. googleMaps API를 통해 위치등록 가능, 범위설정 가능)``
- SensorManager 을 이용하여 ShakeAlgorithm 을 적용한 사용자 인증 
 ``(좌우로 일정 세기 이상 흔들 경우 블루투스 통신)``
- 상속관계를 이용한 서버통신의 다형성(polymorphism) 구현
- 비동기 작업을 위한 스레드 관리
- Callback을 이용한 서버 통신 관리
- BluetoothManager 를 통한 아두이노 (HC-06)모듈과의 통신
``Bluetooth(3.0) 페어링 통신``
  
### 2.Server
- Java Socket Database Server 구축
- Json을 이용한 통신 프토로콜 구축
- MariaDB를 이용한 데이터베이스 스키마, 테이블간의 연관관계 구축
- 로그인, 회원가입 모듈 제작
- Strategy Pattern을 이용하여 코드 간결화
  
### 3.Module
- 타이머를 이용하여 스탭모터와 LED 조작
- Bluetooth 통신을 받아 안드로이드와 통신
- 적외선 센서를 이용하여 사용자 이동을 감지
  
### 4.그 이외 기술
- SingleTon, Strategy, Viewholder 등 Design Pattern적용
- SHA-256 알고리즘을 사용하여 사용자 비밀번호 암호화
  
### 아두이노 모듈
 - Bluetooth HC-06 (Bluetooth 3.0)
 - 전동모터
 - LED
 - UNO
 - 적외선 센서
 - 미니빵판
