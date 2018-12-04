package com.example.user.dooropenservice.app.Server.ServerConnection;

import com.example.user.dooropenservice.app.Model.UserVO;
import com.example.user.dooropenservice.app.Server.ServerCallbackInterface.IDuplicationCallback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

import javax.security.auth.callback.Callback;
/*
 * ServerDuplicateID Thread
 * 서버 DB에 아이디가 중복되는지 확인하는 스레드
 * function : 서버와의 통신(송수신)
 * 현재 아이디가 서버 DB에 존재하는지 확인
 * 상호작용 : SignUpActivity
 * @Author : 조재영
 */
public class ServerDuplicateID extends ServerConnection {
    protected String Result = ""; //서버에서 날라온 결과를 저장하는 String

    protected BufferedReader reader;//데이터 수신객체
    protected BufferedWriter writer;//데이터 전송객체

    public static final int DUPLICATE_ID = 4; //아이디 중복
    public static final int SUCCESS = 5;//연결 성공

    public ServerDuplicateID(UserVO user, Callback callback) {
        super(user, callback);

    }
    @Override
    public void run() {
        super.run();
        if (writer != null) {
            sendData();
        }
        if (reader != null) {
            Thread ReceiveData = new Thread() {
                @Override
                public void run() {
                    //데이터 읽어들이기
                    try {
                        String line;

                        line = reader.readLine();
                        Result = line;


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //결과값에 대한 실행
                    if (Result != null) {//결과값이 돌아왓을 때
                        int flag = Integer.parseInt(Result);
                        switch (flag) {
                            case DUPLICATE_ID :
                                ((IDuplicationCallback)callback).Duplicate_ID();
                                break;
                            case SUCCESS:
                                ((IDuplicationCallback)callback).access();//인증성공
                                break;
                        }

                    }
                    try {
                        reader.close();
                        writer.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            };
            ReceiveData.setName("ReceiveData with Dup");
            ReceiveData.start();
        }
    }

    @Override
    protected void settingSocket() {
        super.settingSocket();
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (IOException e){
            e.printStackTrace();
        }
    }

    @Override
    protected void sendData() {
        PrintWriter out = new PrintWriter(writer, true);
        out.println(getJsonObj());
    }
}
