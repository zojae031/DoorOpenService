package com.example.user.dooropenservice.app.Server.ServerConnection;

import com.example.user.dooropenservice.app.Model.UserVO;
import com.example.user.dooropenservice.app.Server.ServerCallbackInterface.ILogoutCallback;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

/*
 * ServerLogOut Thread
 * 서버에 로그아웃을 요청하는 스레드
 * function : 서버와의 통신(송신만)
 * 현재 로그인되어있는 정보를 서버에서 변경한다.
 * 상호작용 : MainActivity
 * @Author : 조재영
 */
public class ServerLogOut extends ServerConnection {

    protected BufferedWriter writer;//데이터 전송객체

    public ServerLogOut(UserVO userID, ILogoutCallback callback) {
        super(userID, callback);
    }

    @Override
    public void run() {
        super.run();
        try {
            if (writer != null) {
                sendData();//데이터 보내기
            }
            if (writer != null)
                writer.close();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    protected void sendData()   {
        PrintWriter out = new PrintWriter(writer, true);
        out.println(getJsonObj());
    }

    @Override
    protected void settingSocket() {
        super.settingSocket();
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
