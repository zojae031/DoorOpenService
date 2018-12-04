package com.example.user.dooropenservice.app.Server.ServerConnection;

import com.example.user.dooropenservice.app.Model.CompanyVO;

import java.io.BufferedWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;

public class ServerRegistNewLocation extends ServerConnection {

    protected BufferedWriter writer;//데이터 전송객체

    public ServerRegistNewLocation(CompanyVO companyVO) {
        super(companyVO);
    }

    @Override
    public void run() {
        super.run();
        if (writer != null) {
            sendData();//데이터 보내기
        }
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

    @Override
    protected void sendData() {
        PrintWriter out = new PrintWriter(writer, true);
        out.println(getJsonObj());
    }
}
