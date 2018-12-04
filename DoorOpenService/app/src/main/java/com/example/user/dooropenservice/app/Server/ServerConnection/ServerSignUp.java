package com.example.user.dooropenservice.app.Server.ServerConnection;

import com.example.user.dooropenservice.app.Model.UserVO;
import com.example.user.dooropenservice.app.Server.ServerCallbackInterface.ISignUpCallback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
/*
 * ServerSignUp Thread
 * 서버에 회원가입을 요청하는 스레드
 * function : 서버와의 통신(송수신)
 * 새로운 사용자데이터를 서버DB에 저장한다.
 * 상호작용 : SignUpActivity
 * @Author : 조재영
 */
public class ServerSignUp extends ServerConnection {

    protected String Result = ""; //서버에서 날라온 결과를 저장하는 String

    protected BufferedReader reader;//데이터 수신객체
    protected BufferedWriter writer;//데이터 전송객체

    @Override
    protected void settingSocket() {
        super.settingSocket();
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    public static final int SUCCESS = 5;//연결 성공

    public ServerSignUp(UserVO user, ISignUpCallback callback) {
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
                            case SUCCESS:
                                ((ISignUpCallback) callback).accessID();//인증성공
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
            ReceiveData.setName("ReceiveData with SignUp");
            ReceiveData.start();
        }

        /*
         * 서버에서 데이터를 받아
         * 1. 아이디가 있는지 없는지 체크 콜백이용 결과산출
         * 2. 성공하면 콜백이용 Dooropenservice실행
         */
    }

    @Override
    protected void sendData() {
        PrintWriter out = new PrintWriter(writer, true);
        out.println(getJsonObj());
    }
}
