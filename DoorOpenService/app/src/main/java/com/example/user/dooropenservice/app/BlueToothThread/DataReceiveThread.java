package com.example.user.dooropenservice.app.BlueToothThread;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;

/*
 * DataReceiveThread
 * BlueTooth에서 데이터를 수신하기위한 스레드
 * function :  아두이노에서 데이터를 받아온다
 * 상호작용 : BluetoothThread
 * @Author : 조재영
 */
public class DataReceiveThread extends Thread {
    //수신용 객체
    InputStream inputStream;
    BluetoothSocket socket;
    //수신용 변수
    byte[] readBuffer;
    int readBufferPosition;
    final byte responseResult = 100;

    Thread thread;//BlueTooth Thread의 정보를 가져올 객체

    public DataReceiveThread(BluetoothThread thread, BluetoothSocket socket) throws IOException {
        this.socket = socket;
        this.thread = thread;

        inputStream = this.socket.getInputStream();

        readBufferPosition = 0;                 // 버퍼 내 수신 문자 저장 위치.
        readBuffer = new byte[1024];            // 수신 버퍼.

    }


    @Override
    public void interrupt() {
        try {
            if (inputStream != null) {
                inputStream.close();
            }
            thread.interrupt();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void run() {

        try {
            State state = Thread.currentThread().getState();
            while (!Thread.currentThread().isInterrupted()) {
                // InputStream.available() : 다른 스레드에서 blocking 하기 전까지 읽은 수 있는 문자열 개수를 반환함.
                int byteAvailable = inputStream.available();   // 수신 데이터 확인
                if (byteAvailable > 0) {                        // 데이터가 수신된 경우.
                    byte[] packetBytes = new byte[byteAvailable];
                    // read(buf[]) : 입력스트림에서 buf[] 크기만큼 읽어서 저장 없을 경우에 -1 리턴.
                    inputStream.read(packetBytes);
                    for (int i = 0; i < byteAvailable; i++) {
                        byte b = packetBytes[i];
                        if (b == responseResult) {//결과값이 같으면
                            readBufferPosition = 0;

                            //현재 쓰레드에 인터럽트를 걸어줌 (Exception 을 걸어 while문을 빠져나오는 형식)
                            //소켓을 닫아버림으로써 IOException 발생하며 while문 탈출
                            this.interrupt();


                        } else {
                            readBuffer[readBufferPosition++] = b;
                        }
                    }
                }
            }//end of while
        } catch (IOException e) {    // 데이터 수신 중 오류 발생.
            e.printStackTrace();
        }
    }
}

