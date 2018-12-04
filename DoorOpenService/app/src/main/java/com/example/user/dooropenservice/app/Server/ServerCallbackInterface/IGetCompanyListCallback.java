package com.example.user.dooropenservice.app.Server.ServerCallbackInterface;

import com.example.user.dooropenservice.app.Model.CompanyVO;

import java.util.ArrayList;

public interface IGetCompanyListCallback extends IServerCallback {
    void sendData(final ArrayList<CompanyVO> companyVOArrayList);
}
