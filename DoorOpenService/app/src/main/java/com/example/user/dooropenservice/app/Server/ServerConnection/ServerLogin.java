package com.example.user.dooropenservice.app.Server.ServerConnection;

import com.example.user.dooropenservice.app.Model.UserVO;
import com.example.user.dooropenservice.app.Server.ServerCallbackInterface.ILoginCallback;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/*
 * ServerLogin
 * 서버에 로그인을 요청하는 스레드
 * function : 서버와의 통신 (송, 수신)
 * 입력받은 정보로 서버에 로그인작업을 요청
 * 로그인결과에 해당하는 동작 실행 (ILoginCallback)
 * 상호작용 : LoginActivity , ILoginCallback
 */
public class ServerLogin extends ServerConnection {
    protected String Result = ""; //서버에서 날라온 결과를 저장하는 String

    public static final int LOGIN_OK = 1; //로그인 성공
    public static final int NO_DATA = 2; //데이터베이스에 저장된 정보 없음
    public static final int LOGIN_FAIL = 3; //로그인 실패

    protected BufferedReader reader;//데이터 수신객체
    protected BufferedWriter writer;//데이터 전송객체

    public ServerLogin(UserVO user, ILoginCallback callback) {
        super(user,callback);
    }

    @Override
    public void run() {
        super.run();
         if (writer != null) {
            sendData();//데이터 보내기
        }
        if (reader != null) {
            //데이터 받는 내부 스레드
            Thread ReceiveData = new Thread() {
                @Override
                public void run() {
                    //데이터 읽어들이기
                    try {
                        String line;
                        while (true) {
                            line = reader.readLine();
                            Result = line;
                            if ((Result.equals(String.valueOf(LOGIN_OK))) || (Result.equals(String.valueOf(LOGIN_FAIL)))||(Result.equals(String.valueOf(NO_DATA))))
                                break; //true 나 false 가 돌아왔을 때는 서버와 통신이 되었다는 결과 (왜? 서버에서 그러도록 내가 짰으니까 ㅋ
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //결과값에 대한 실행
                    if (Result!=null) {//결과값이 돌아왓을 때
                        int flag = Integer.parseInt(Result);
                        switch (flag){
                            case LOGIN_OK :((ILoginCallback)callback).StartService();//인증성공
                                break;
                            case NO_DATA :((ILoginCallback)callback).NoData();//인증실패(ID나 PASSWORD 중 하나가 잘못됨)
                                break;
                            case LOGIN_FAIL : ((ILoginCallback)callback).FailToLogin();//DB에 데이터가 없는 경우
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
            ReceiveData.setName("ReceiveData");
            ReceiveData.start(); //데이터 받아오기
        }
    }
    @Override
    protected void sendData() {
        PrintWriter out = new PrintWriter(writer, true);
        out.println(getJsonObj());
    }
    @Override
    protected void settingSocket(){
        super.settingSocket();
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
        }catch (IOException e){
            e.printStackTrace();
        }
    }
}
