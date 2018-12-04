package com.example.user.dooropenservice.app.Server.ServerConnection;

import com.example.user.dooropenservice.app.Model.CompanyVO;
import com.example.user.dooropenservice.app.Model.UserVO;
import com.example.user.dooropenservice.app.Server.ServerCallbackInterface.ICompanyCheckCallback;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.util.ArrayList;

import javax.security.auth.callback.Callback;

/*
 * ServerCompanyCheck
 * 서버에서 로그인된 ID의 Company 정보를가져오는 클래스
 * function : MainActivity에서 DoorOpenService(GPS) 를 실행하기 전에 서버에서 GPS정보를 가져오는 클래스
 * 상호작용 : MainActivity
 */
public class ServerCompanyCheck extends ServerConnection {
    protected BufferedReader reader;//데이터 수신객체
    protected BufferedWriter writer;//데이터 전송객체

    ArrayList<CompanyVO> companyVOArrayList = new ArrayList<>();

    public ServerCompanyCheck(UserVO user, Callback callback) {
        super(user, callback);
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
                        //서버에서 받아온 JsonArray 를 파싱하여 ArrayList 로 옮겨담는 작업
                        JsonParser parser = new JsonParser();
                        JsonArray jsonArray= (JsonArray) parser.parse(reader.readLine());
                        for(int i=0 ;i<jsonArray.size() ; i++){
                            JsonObject data = (JsonObject) jsonArray.get(i);
                            companyVOArrayList.add(new CompanyVO(data.get("company").toString(),data.get("latitude").getAsDouble() ,data.get("longitude").getAsDouble() ,data.get("scope").getAsDouble()));
                        }


                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //서비스 실행
                    ((ICompanyCheckCallback) callback).startService(companyVOArrayList);



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
    protected void settingSocket() {
        super.settingSocket();
        try {
            writer = new BufferedWriter(new OutputStreamWriter(socket.getOutputStream()));
            reader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
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
