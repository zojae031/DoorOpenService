package com.example.user.dooropenservice.app.Server.ServerConnection;

import com.example.user.dooropenservice.app.Model.CompanyVO;
import com.example.user.dooropenservice.app.Model.UserVO;
import com.example.user.dooropenservice.app.Server.ServerCallbackInterface.IGetCompanyListCallback;
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



public class ServerGetCompanyList extends ServerConnection {

    ArrayList<CompanyVO> companyVOArrayList = new ArrayList<>();

    protected BufferedReader reader;//데이터 수신객체
    protected BufferedWriter writer;//데이터 전송객체
    public ServerGetCompanyList(UserVO user, Callback callback) {
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
                        JsonParser parser = new JsonParser();
                        JsonArray jsonArray= (JsonArray) parser.parse(reader.readLine());
                        for(int i=0 ;i<jsonArray.size() ; i++){
                            JsonObject data = (JsonObject) jsonArray.get(i);
                            companyVOArrayList.add(new CompanyVO(data.get("company").toString(),0.0,0.0,0.0));
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    //결과값에 대한 실행
                    ((IGetCompanyListCallback)callback).sendData(companyVOArrayList);


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
    protected void sendData() {
        PrintWriter out = new PrintWriter(writer, true);
        out.println(getJsonObj());
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
}
