
package com.example.user.dooropenservice.app.BlueToothThread;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.bluetooth.BluetoothSocket;
import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.example.user.dooropenservice.app.ShakeAlgorithm.IShakeCallback;
import com.example.user.dooropenservice.app.ShakeAlgorithm.ShakeService;

import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

/*
 * BluetoothThread
 * BlueTooth를 전체적으로 관리하는 서비스 클래스
 * Bluetooth연결의 기본동작과 기기선택을 관리함
 * function : 페어링된 블루투스 선택, 블루투스로 데이터를 송신
 * 상호작용 : DataReceiveThread, ShakeService
 * @Author : 조재영
    Pairing : 보안 기능 교환, 서로 연결을 하도록 허용하는 작업
    Bonding : 블루투스가 연결되어 데이터를 주고받을 수 있는 상태

 */
public class BluetoothThread extends Thread {
    //블루투스 통신을 위한 UUID정보
    final String base_UUID = "00001101-0000-1000-8000-00805F9B34FB"; //Base UUID
    UUID uuid = UUID.fromString(base_UUID); //UUID를 고유값으로 설정

    //안드로이드 어플리케이션 점보
    Context context;

    // 블루투스 관련 객체
    BluetoothDevice RemoteDevice; //아두이노와 연결될 객체
    Set<BluetoothDevice> device; //Bluetooth의 페어링 된 정보를 가지는 Set
    String arduino = ""; //사용할 아두이노 이름정보를 저장할 공간
    BluetoothAdapter bluetoothAdapter;


    //통신용 변수
    BluetoothSocket socket = null;

    OutputStream outputStream;

    //수신 전용 변수

    DataReceiveThread dataReceiveThread = null;


    //콜백을 위한 객체
    IShakeCallback shakeCallback;


    public BluetoothThread(ShakeService shakeService, Context context) {
        //callback메소드를 쓰기위한 객체 정보 얻어오기
        shakeCallback = shakeService;
        this.context = context;

    }

    @Override
    public void run() {
        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter(); //어댑터 정보 가져오기
        BluetoothStart(); //Bluetooth실행
    }

    @Override
    public void interrupt() {

            shakeCallback.registerListener(); //Shake 리스너 재등록


        bluetoothAdapter = null;
        if (socket != null) {
            try {
                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }

            if (dataReceiveThread != null) {
                dataReceiveThread = null;
            }
        }
    }


    /*
     * Bluetooth Adapter에대한 셋팅을 하는 메소드
     * 어댑터를 설정하고, 기기가 블루투스 사용가능상태인지 아닌지 검사한다.
     * 페어링 된 장치가 있으면 선택된 디바이스로 연결을 시도한다.
     */
    private void BluetoothStart() {


        //블루투스를 지원하며 활성상태일 경우 페어링된 기기 목록을 가져와 아두이노를 연결
        device = bluetoothAdapter.getBondedDevices();


        if (device.size() > 0) { //Bond 된 장치가 있는 경우
            selectDevice(); //기기 선택
            connectToSelectedDevices(arduino); //선택된 기기로 연결 시도

        } else { //Bond 된 장치가 없는 경우
            Toast.makeText(context, "장치가 없습니다.", Toast.LENGTH_SHORT).show();
            this.interrupt();
        }

    }




    /*
       블루투스 기기를 List에 집어넣고
       페어링 된 기기가 있으면 우리가 사용할 기기정보를 저장해주는 메소드
     */
    private void selectDevice() {

        //페어링 된 블루투스 장치의 이름을 저장
        List<String> list = new ArrayList<String>();
        for (BluetoothDevice searchDevice : device) {
            list.add(searchDevice.getName());
        }

        /*
        # 다이얼로그를 띄워서 선택된 디바이스정보를 저장하는 로직 작성필요
        arduino에 저장
         */
        if (list.size() > 0) {
            for (String selectedDevice : list) {
                if (selectedDevice.contains("HC-06")) { //"HC-06"을 포함하고 있으면 변수에 저장
                    arduino = selectedDevice.toString();
                }
            }
        }

    }


    /*
     디바이스의 이름을 찾아 연결을 시도하는 메소드
     */
    void connectToSelectedDevices(String selectedDeviceName) {
        RemoteDevice = getDeviceFromBondedList(selectedDeviceName);


        try {
            // 소켓 생성
            socket = RemoteDevice.createRfcommSocketToServiceRecord(uuid);

            // RFCOMM 채널을 통한 연결
            socket.connect();

            // 데이터 송수신을 위한 스트림 열기
            outputStream = socket.getOutputStream();


            // 데이터 송신
            sendData("1");

            // 데이터 수신 준비
            beginListenForData();


        }
         catch (IOException e) {
            //timeOut일떄
             Log.e("ShakeBluetooth TimeOut",e.getMessage());
             this.interrupt();
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    /*
    데이터 전송 함수
     */
    void sendData(String msg) throws Exception {

        outputStream.write(msg.getBytes());    // 문자열 전송


    }

    /*
    원하는 기기가 리스트에 존재하면 그 디바이스 정보를 가져오는 메소드
    @return 선택된 디바이스
     */
    BluetoothDevice getDeviceFromBondedList(String name) {
        BluetoothDevice selectedDevice = null;

        for (BluetoothDevice searchDevice : device) {
            if (name.equals(searchDevice.getName())) {
                selectedDevice = searchDevice;
                break;
            }
        }
        return selectedDevice;
    }

    // 데이터 수신(쓰레드 사용 수신된 메시지를 계속 검사함)
    void beginListenForData() throws IOException {

        // 문자열 수신 쓰레드.
        dataReceiveThread = new DataReceiveThread(this, socket);
        dataReceiveThread.setName("ReceiveThread");
        dataReceiveThread.start();

    }


}
